package fr.uparis.zhou.mobiles_projet

import android.annotation.SuppressLint
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

@Suppress("SpellCheckingInspection")
class ApprentissageService : Service() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var notificationManager: NotificationManager
    private lateinit var alarmManager: AlarmManager
    private lateinit var myDao: MyDao
    private lateinit var listmots: List<Mot>
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        //On rajoute une variable dans les sharedPreferences qui nous dit de tout remettre à false
        sharedPreferences.edit().putInt("unseeAll", 1).apply()
        stopSelf()
    }

    @SuppressLint("UnspecifiedImmutableFlag")
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
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!sharedPreferences.getBoolean("alarm", true)) {
            return START_NOT_STICKY
        } else {

            val stopIntent = Intent(
                    this,
                    ApprentissageService::class.java
            ).apply {
                action = "STOP"
            }
            val pendingStopIntent =
                    PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_CANCEL_CURRENT)
            val foreNotif = NotificationCompat.Builder(this, "channel_id")
                    .setContentTitle("Notifications")
                    .setContentText("On en apprend des choses tous les jours !")
                    .setSmallIcon(R.drawable.idea)
                    .addAction(R.drawable.idea, "STOP SERVICE", pendingStopIntent)
                    .setAutoCancel(true)
                    .build()
            startForeground(
                    1,
                    foreNotif
            )
            //var notificationID = intent?.getIntExtra("notifID", -1) ?: -1
            var notificationID = sharedPreferences.getInt("notifID", 1)
            var motID: String?
            if (intent != null) {
                if (intent.action == "STOP") {
                    //val alarmIntent = Intent(this, ApprentissageService::class.java)
                    //alarmManager.cancel(PendingIntent.getBroadcast(this, 0, alarmIntent, 0))
                    sharedPreferences.edit().putBoolean("alarm", false).apply()
                    notificationManager.cancelAll()
                    //On rajoute une variable dans les sharedPreferences qui nous dit de tout remettre à false
                    sharedPreferences.edit().putInt("unseeAll", 1).apply()
                    Log.d("Service", "Terminé")
                    stopSelf()
                    return START_NOT_STICKY
                }

                if (intent.action == "SEARCH") {
                    val url = intent.getStringExtra("url")
                    Log.d("reçue", "$url")
                    val id = intent.getIntExtra("notifIDelete", -1)
                    Log.d("id reçu", "$id")
                    val browserIntent = Intent(Intent.ACTION_VIEW)
                    browserIntent.data = Uri.parse(url)
                    browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(browserIntent)
                    notificationManager.cancel(id)
                    thread {
                        motID = intent.getStringExtra("motID")
                        if (motID != null) {
                            val mot = myDao.getMotbyID(motID!!)
                            mot.used = false
                            myDao.updateMots(mot)
                            Log.d("mot ${mot.mot}", " devient FALSE")
                        }
                    }
                    return START_NOT_STICKY
                }
            }

            // Tout le bloc en haut sert à relancer le service chaque interval millisecondes
            //val mois: Long = 30L * 24L * 60L * 60L * 1000L
            //val tmpTemps = 60000L //60s
            val temps = sharedPreferences.getInt("tmpsappris", 14)//Par défaut c'est 14 jours
            val datecurr: Long = System.currentTimeMillis()
            val difftemps: Long = datecurr - (temps * 24 * 60 * 60 * 1000)
            var nb: Int
            val relanceAlarm = sharedPreferences.getBoolean("alarm", true)
            //On fait les modifications sur la bd dans un thread
            thread {

                val tmpAllMots = myDao.loadEverything()
                if (sharedPreferences.getInt("unseeAll", 0) == 1) {
                    for (i in tmpAllMots) {
                        i.used = false
                        myDao.updateMots(i)
                    }
                    sharedPreferences.edit().putInt("unseeAll", 0).apply()
                    Log.d("Unsee ALL", "SUCCESS")
                } else {
                    Log.d("Unsee ALL", "FAILURE")
                }

                motID = intent?.getStringExtra("motID")
                if (motID != null) {
                    val mot = myDao.getMotbyID(motID!!)
                    mot.used = false
                    myDao.updateMots(mot)
                    Log.d("mot ${mot.mot}", " devient FALSE")
                }
                nb = intent?.getIntExtra(
                        "nb", sharedPreferences.getInt(
                        "nbMots", 10
                )
                ) ?: 10
                Log.d("nb", "$nb")
                //Dans paramètre, quand on choisit 0 mots, tout s'arrete
                // On reçoit toute de même la notification journalière qui demande de relancer
                if (sharedPreferences.getInt("nbMots", -1) == 0) {
                    nb = 0
                }
                Log.d("BD -> listmots", "Chargement")


                //On vérifie quel jour de la semaine on est et on charge en
                // fonction du jour et des paramètres
                val calendar = Calendar.getInstance()
                val maitrisenb = sharedPreferences.getInt("nbmaitrise", 2)//Par défaut 2 swipes
                when (calendar.get(Calendar.DAY_OF_WEEK)) {
                    Calendar.MONDAY -> {
                        val dimPref = sharedPreferences.getString("lundi", "") ?: ""
                        listmots = if (dimPref != "") {
                            val separe = dimPref.split("-")
                            val src = separe[0]
                            val dst = separe[1]
                            myDao.loadTenWordsBis(nb, difftemps, maitrisenb, src, dst)
                        } else {
                            myDao.loadTenWords(nb, difftemps, maitrisenb)
                        }
                    }
                    Calendar.TUESDAY -> {
                        val dimPref = sharedPreferences.getString("mardi", "") ?: ""
                        listmots = if (dimPref != "") {
                            val separe = dimPref.split("-")
                            val src = separe[0]
                            val dst = separe[1]
                            myDao.loadTenWordsBis(nb, difftemps, maitrisenb, src, dst)
                        } else {
                            myDao.loadTenWords(nb, difftemps, maitrisenb)
                        }
                    }
                    Calendar.WEDNESDAY -> {
                        val dimPref = sharedPreferences.getString("mercredi", "") ?: ""
                        listmots = if (dimPref != "") {
                            val separe = dimPref.split("-")
                            val src = separe[0]
                            val dst = separe[1]
                            myDao.loadTenWordsBis(nb, difftemps, maitrisenb, src, dst)
                        } else {
                            myDao.loadTenWords(nb, difftemps, maitrisenb)
                        }
                    }
                    Calendar.THURSDAY -> {
                        val dimPref = sharedPreferences.getString("jeudi", "") ?: ""
                        listmots = if (dimPref != "") {
                            val separe = dimPref.split("-")
                            val src = separe[0]
                            val dst = separe[1]
                            myDao.loadTenWordsBis(nb, difftemps, maitrisenb, src, dst)
                        } else {
                            myDao.loadTenWords(nb, difftemps, maitrisenb)
                        }
                    }
                    Calendar.FRIDAY -> {
                        val dimPref = sharedPreferences.getString("vendredi", "") ?: ""
                        listmots = if (dimPref != "") {
                            val separe = dimPref.split("-")
                            val src = separe[0]
                            val dst = separe[1]
                            myDao.loadTenWordsBis(nb, difftemps, maitrisenb, src, dst)
                        } else {
                            myDao.loadTenWords(nb, difftemps, maitrisenb)
                        }
                    }
                    Calendar.SATURDAY -> {
                        val dimPref = sharedPreferences.getString("samedi", "") ?: ""
                        listmots = if (dimPref != "") {
                            val separe = dimPref.split("-")
                            val src = separe[0]
                            val dst = separe[1]
                            myDao.loadTenWordsBis(nb, difftemps, maitrisenb, src, dst)
                        } else {
                            myDao.loadTenWords(nb, difftemps, maitrisenb)
                        }
                    }
                    Calendar.SUNDAY -> {
                        val dimPref = sharedPreferences.getString("dimanche", "") ?: ""
                        listmots = if (dimPref != "") {
                            val separe = dimPref.split("-")
                            val src = separe[0]
                            val dst = separe[1]
                            myDao.loadTenWordsBis(nb, difftemps, maitrisenb, src, dst)
                        } else {
                            myDao.loadTenWords(nb, difftemps, maitrisenb)
                        }
                    }
                }

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
/*
                val mot = listmots[i]
                mot.used = false
                Log.d("mot ${mot.mot}", "devient false encore ")
                myDao.updateMots(mot)
*/
                }
                Log.d("count", count.toString())
                Log.d("sizeUNSEEN", unseenMots.size.toString())
                // Envoi des Notifications
                val size = min(nb - count, unseenMots.size) // meme si negatif pas grave
                Log.d("size", "$nb $size")

                if (intent != null) {
                    for (i in 0 until size) {
                        notificationID++
                        sharedPreferences.edit().putInt("notifID", notificationID).apply()
                        val m = unseenMots[i]
                        val cancelIntent = Intent(
                                this,
                                ApprentissageService::class.java
                        ).apply {
                            action = "SEARCH"
                            putExtra("url", m.url)
                            Log.d("url envoyée", m.url)
                            putExtra("motID", m.mot)
                            putExtra("notifIDelete", notificationID)
                            Log.d("id envoyé", "$notificationID")
                        }


                        val repeatIntent = Intent(
                                this,
                                ApprentissageService::class.java
                        ).apply {
                            action = "REPEAT"
                            putExtra("nb", 1)
                            putExtra("motID", m.mot)
                            Log.d("motID envoyé", m.mot)
                        }

                        val pendingRepeatIntent =
                                PendingIntent.getService(this, notificationID, repeatIntent, 0)
                        val pendingBrowserIntent = PendingIntent.getService(
                                this, notificationID, cancelIntent, 0
                        )
                        val notification = NotificationCompat.Builder(this, "channel_id")
                                .setContentTitle("${m.src} -> ${m.dst}")
                                .setContentText(m.mot)
                                .setSmallIcon(R.drawable.idea)
                                .setAutoCancel(true)
                                .setDeleteIntent(pendingRepeatIntent)
                                .addAction(R.drawable.idea, "Rechercher", pendingBrowserIntent)
                                .build()
                        notificationManager.notify(notificationID, notification)
                        thread {
                            if (intent.action == "REPEAT") {
                                m.maitrise = m.maitrise?.plus(1)
                            }
                            m.used = true
                            m.lastVu = System.currentTimeMillis()
                            myDao.updateMots(m)
                        }
                        Log.d(
                                "mot, vu, dernier vu",
                                "${m.mot} ${m.maitrise} ${Date((m.lastVu!!))}"
                        )
                    }
                    Log.d(
                            "size des notifs actives",
                            "${notificationManager.activeNotifications.size}"
                    )
                    Log.d("size du unseen", "${unseenMots.size}")
                    //s'il y a un probleme, on revert
                    if (notificationManager.activeNotifications.size < unseenMots.size) {
                        for (i in unseenMots.indices) {
                            val change = unseenMots[i]
                            change.used = false
                            myDao.updateMots(change)
                            Log.d("end_for", "mot reverted used value because of error")
                        }
                    }

                    //SI c'est juste l'intent après avoir fermé une notification (swipe ou autre),
                    // on ne met pas d'alarme
                    if (intent.action != "REPEAT" && intent.action != "SEARCH") {
                        //Interval de relance du service:
                        // pour les tests chaque 10 secondes, pour la version finale, on
                        // récupère dans les paramètres la fréquence choisie et on l'étale sur 1 jour
                        val frequence = sharedPreferences.getInt("freqMots", 3000)
                        val interval = (24 * 60 * 60 * 1000L / frequence)
                        // frequence determine le nombre de fois que l'alarme sera déclenchée par jour
                        //intent.putExtra("notifID", tmpNotif)
                        val pendingIntent = intent.let {
                            PendingIntent.getService(
                                    this,
                                    0, it, 0
                            )
                        }
                        alarmManager.setExact(
                                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                                SystemClock.elapsedRealtime() + interval,
                                pendingIntent
                        )
                        if (!relanceAlarm) {
                            alarmManager.cancel(pendingIntent)
                        } else {
                            Log.d(
                                    "PROCHAINE ALARME DANS", (interval / 1000).toString() +
                                    " secondes"
                            )
                        }
                    }
                }
            }
            return START_STICKY
        }
    }
}
