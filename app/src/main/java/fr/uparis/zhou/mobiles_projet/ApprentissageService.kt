package fr.uparis.zhou.mobiles_projet

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.min

class ApprentissageService : Service() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var notificationManager: NotificationManager
    private lateinit var alarmManager: AlarmManager
    private lateinit var myDao: MyDao
    private lateinit var listmots: List<Mot>
    private var notificationID = 0
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSelf()
    }

    override fun onCreate() {
        super.onCreate()

        //SharedPreferences
        sharedPreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE)

        //NotificationManager
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //AlarmManager
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        //NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "channel_id",
                "channel_name",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        //myDao
        myDao = (application as DicoApplication).database.myDao()
        val foreNotif = NotificationCompat.Builder(this, "channel_id")
            .setContentTitle("Notifications")
            .setContentText("On en apprend des choses tous les jours !")
            .setSmallIcon(R.drawable.idea)
            .setAutoCancel(true)
            .build()
        startForeground(
            11,
            foreNotif
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        var motID :String? = ""
        if (intent != null) {
            if (intent.action == "SEARCH") {
                val url = intent.getStringExtra("url")
                val id = intent.getIntExtra("id", -1)
                val browserIntent = Intent(Intent.ACTION_VIEW)
                browserIntent.data = Uri.parse(url)
                browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(browserIntent)
                notificationManager.cancel(id)
                return START_NOT_STICKY
            }
            //SI c'est juste l'intent après avoir fermé une notification (swipe ou autre), on ne met pas d'alarme
            if (intent.action != "REPEAT" && intent.action != "SEARCH") {
                //Interval de relance du service:
                // pour les tests chaque 10 secondes, pour la version finale, on récupère dans les paramètres
                // la fréquence choisie et on l'étale sur 1 jour
                val frequence = sharedPreferences.getInt("freqMots", 1500)// Chaque 57 secondes
                var interval = (24 * 60 * 60 * 1000L / frequence)
                // frequence determine le nombre de fois que l'alarme sera déclenchée par jour
                val pendingIntent = intent?.let { PendingIntent.getService(this, 12, it, 0) }
                alarmManager.setExact(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + interval,
                    pendingIntent
                )
                Log.d("PROCHAINE ALARME DANS", (interval / 1000).toString() + " secondes")
            }else{
                motID = intent.getStringExtra("motID")
            }
        }
        // Tout le bloc en haut sert à relancer le service chaque interval millisecondes
        val mois: Long = 30L * 24L * 60L * 60L * 1000L
        val tmpTemps = 300000L //300s
        val datecurr: Long = System.currentTimeMillis()
        var nb: Int
        var id: Int
        //On fait les modifications sur la bd dans un thread
        thread {
            nb = intent?.getIntExtra("nb", sharedPreferences.getInt("nbMots", 10)) ?: 10

            //Dans paramètre, quand on choisit 0 mots, tout s'arrete
            // On reçoit toute de même la notification journalière qui demande de relancer
            if (sharedPreferences.getInt("nbMots", -1) == 0) {
                nb = 0
            }
            Log.d("BD -> listmots", "Chargement")
            if (motID != null) {
                val mot = myDao.getMotbyID(motID)
                if(mot != null){
                    mot.used = false
                    myDao.updateMots(mot)
                }
            }
            listmots = myDao.loadTenWords(nb, datecurr - tmpTemps)
            val unseenMots: MutableList<Mot> = mutableListOf()
            var count = 0
            Log.d("sizelistmo", listmots.size.toString())
            for (i in listmots.indices) {
                if (listmots[i].used == true) {
                    count++
                } else {
                    //On ajoute les mots à afficher ici
                    Log.d("avant add", "notadding yet")
                    if (!unseenMots.contains(listmots[i])) {
                        unseenMots.add(listmots[i])
                        Log.d("adding", "adding")
                    }
                }
                //val mot = listmots[i]
                //mot.used = false
                //myDao.updateMots(mot)
            }
            Log.d("sizeUNSEEN", unseenMots.size.toString())
            // Envoi des Notifications
            //val size = listmots.size
            val size = min(nb - count, unseenMots.size) // meme si negatif pas grave
            Log.d("size", "$nb $size")

            for (i in 0 until size) {
                id = intent?.getIntExtra("id", i) ?: i
                //val m = listmots[i]
                val m = unseenMots[i]
                val cancelIntent = Intent(this, ApprentissageService::class.java).apply {
                    action = "SEARCH"
                    putExtra("url", m.url)
                    putExtra("motID",m.mot)

                    /*if (intent?.action == "REPEAT") {
                        putExtra("id", id)
                    } else {
                        putExtra("id", i)
                    }*/
                    putExtra("id", notificationID)
                }

                val repeatIntent = Intent(this, ApprentissageService::class.java).apply {
                    action = "REPEAT"
                    putExtra("nb", 1)
                    putExtra("motID",m.mot)

                    /*if (intent?.action == "REPEAT") {//c'est la deuxième répétition
                        putExtra("id", id)
                    } else {
                        putExtra("id", i)
                    }*/
                    putExtra("id", notificationID)
                }

                val pendingRepeatIntent = PendingIntent.getService(this, id, repeatIntent, 0)
                val pendingBrowserIntent = PendingIntent.getService(
                    this, id, cancelIntent, 0
                )
                val notification = NotificationCompat.Builder(this, "channel_id")
                    .setContentTitle("Notification $id")
                    .setContentText(m.mot)
                    .setSmallIcon(R.drawable.idea)
                    .setAutoCancel(true)
                    .setDeleteIntent(pendingRepeatIntent)
                    .addAction(R.drawable.idea, "Rechercher", pendingBrowserIntent)
                    .build()
                notificationManager.notify(id, notification)
                thread {
                    if (intent != null) {
                        if(intent.action == "REPEAT"){
                            m.maitrise = m.maitrise?.plus(1)
                        }
                    }
                    //m.used = true
                    m.lastVu = System.currentTimeMillis()
                    myDao.updateMots(m)
                }
                Log.d("mot, vu, dernier vu", "${m.mot} ${m.maitrise} ${Date((m.lastVu!!))}")
                notificationID++
            }

        }
        return Service.START_STICKY
    }
}
