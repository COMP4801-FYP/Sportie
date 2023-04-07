package hk.hkucs.sportieapplication.activities

import android.content.ContentValues.TAG
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.common.io.Files.append
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hk.hkucs.sportieapplication.R
import hk.hkucs.sportieapplication.models.SportCentre
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileReader
import java.io.IOException
import java.lang.Math.min
import kotlin.random.Random

//import com.example.parsejsonexample.Cell

class GeodataActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geodata)


        var btn_add = findViewById<Button>(R.id.btn_add)
        btn_add!!.setOnClickListener {
//            sendMessage()

            var tmptime = mutableListOf<String>()
            var tmpcourt = mutableListOf<String>()
            var tmpaddr = mutableListOf<String>()

            val jsonFileString = getJsonDataFromAsset(applicationContext, "facility-bkbc.json")
            val gson = Gson()
            val listPersonType = object : TypeToken<List<SportCentre>>() {}.type
            var persons: List<SportCentre> = gson.fromJson(jsonFileString, listPersonType)
            persons.forEachIndexed {
                    idx, sportcentre ->
                // ID
                val id = sportcentre.GIHS

                // Facility Name
                val facName = sportcentre.Name_en

                // District
                val district = sportcentre.District_en

                // Address
                val address = sportcentre.Address_en

                // Opening Hours
                val openHour = sportcentre.Opening_hours_en

                // court no
                val courtno = sportcentre.Court_no_en

                // Facility Details
                val details = sportcentre.Remarks_en

                val phone = sportcentre.Phone

                val lat = sportcentre.Latitude

                val long = sportcentre.Longitude

                val facilities = sportcentre.Ancillary_facilities_en

                val geodatas: MutableMap<String, Any> = HashMap()


                if (openHour !in tmptime){
                    tmptime.add(openHour)
                }

                if (courtno !in tmpcourt){
                    tmpcourt.add(courtno)
                }
                if (address !in tmpaddr){
                    tmpaddr.add(address)
                }

                // Extract time using regex
                val (openTime, closeTime) = extractTime(openHour)
                println("$openHour")
                println("Start time: $openTime, Close time: $closeTime")

                if (openTime == ""){
                    print("bgst $openHour $id $facName")
                }

                // remove all HTML tags
                geodatas["ID"] = id
                geodatas["address"] = if (address != null) {
                    address.replace(Regex("<.*?>"), "").replace(Regex("\\s+"), " ").trim()
                } else {
                    // handle the case where details is null
                    ""
                }
                geodatas["district"] = if (district != null) {
                    district.replace(Regex("<.*?>"), "").replace(Regex("\\s+"), " ").trim()
                } else {
                    // handle the case where details is null
                    ""
                }
                geodatas["facility_name"] = if (facName != null) {
                    facName.replace(Regex("<.*?>"), "").replace(Regex("\\s+"), " ").trim()
                } else {
                    // handle the case where details is null
                    ""
                }
                geodatas["opening_hours"] = "$openTime - $closeTime"
                geodatas["facility_details"] = if (details != null) {
                    details.replace(Regex("<.*?>"), "").replace(Regex("\\s+"), " ").trim()
                } else {
                    // handle the case where details is null
                    ""
                }
                geodatas["phone"] = if (phone != null) {
                    phone.replace(Regex("<.*?>"), "").replace(Regex("\\s+"), " ").trim()
                } else {
                    // handle the case where details is null
                    ""
                }
                geodatas["latitude"] = lat
                geodatas["longitude"] = long
                geodatas["facilities"] = if (facilities != null) {
                    facilities.replace(Regex("<.*?>"), "").replace(Regex("\\s+"), " ").trim()
                } else {
                    // handle the case where details is null
                    ""
                }
                geodatas["courtno"] = extractCourtNumber(courtno)

                geodatas["bookmarks"] = Random.nextInt(0, 800)

                var cno = geodatas["courtno"]
                println("courtno $cno $courtno")
                var dis = geodatas["district"]
                println("district $dis")

                saveFireStore2(geodatas)

            }

            // add dummy field
            val geodatas: MutableMap<String, Any> = HashMap()
            geodatas["ID"] = "dummy"
            geodatas["address"] = "Classroom"
            geodatas["bookmarks"] = 10
            geodatas["courtno"] = "2"
            geodatas["district"] = "Central & Western"
            geodatas["facilities"] = "Table and chairs"
            geodatas["facility_details"] = ""
            geodatas["facility_name"] = "Demo Field"
            geodatas["latitude"] = "22-17-24"
            geodatas["longitude"] = "114-8-40"
            geodatas["opening_hours"] = "07:00-23:00"
            geodatas["phone"] = "1232131"
            saveFireStore2(geodatas)
        }
    }

    fun sendMessage(): String {
        val url = "https://geodata.gov.hk/gs/api/v1.0.0/geoDataQuery?q=%7Bv%3A%221%2E0%2E0%22%2Cid%3Afb65b9aa-05d9-4768-a8b1-148072180ba1%2Clang%3A%22ENG%22%7D"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                switchActivity(response) },
            Response.ErrorListener { error ->
                Log.e("MyActivity",error.toString())
            }
        )
        Volley.newRequestQueue(this).add(jsonObjectRequest)
        return "ok"
    }
    fun switchActivity(jsonObj: JSONObject) {
        val jsonArray: JSONArray = jsonObj.getJSONArray("features")


        for (i in 0 until jsonArray.length()) {
            val prop = jsonArray.getJSONObject(i).getJSONObject("properties")
            // ID
            val id = prop.getString("GMID")

            // Facility Name
            val facName = prop.getString("Facility Name")

            // District
            val district = prop.getString("District")

            // Address
            val address = prop.getString("Address")

            // Opening Hours
            val openHour = prop.getString("Opening Hours")

            // Facility Details
            val details = prop.getString("Facility Details")

            val geodatas: MutableMap<String, Any> = HashMap()
            geodatas["ID"] = id
            geodatas["address"] = address
            geodatas["district"] = district
            geodatas["facility_name"] = facName
            geodatas["opening_hours"] = openHour
            geodatas["facility_details"] = details

            saveFireStore(geodatas)

        }
//        val intent = Intent(this, PersonListActivity::class.java).apply {
//            putStringArrayListExtra("data", studentList)
//        }
//        binding.jsonResultsRecyclerview.adapter = adapter
//        startActivity(intent)
    }

    // Sample function to save to firestore
    fun saveFireStore(geodatas: MutableMap<String, Any>){
        val db = FirebaseFirestore.getInstance()

        db.collection("geodata")
            .add(geodatas)
            .addOnSuccessListener {
                Toast.makeText(this@GeodataActivity, "record added successfully", Toast.LENGTH_SHORT).show();
            }
            .addOnFailureListener {
                Toast.makeText(this@GeodataActivity, "record added unsuccessful", Toast.LENGTH_SHORT).show();
            }
    }



    fun getJsonDataFromAsset(context: Context, fileName: String): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }


    fun switchActivity2(jsonObj: JSONObject) {
        val jsonArray: JSONArray = jsonObj.getJSONArray("features")

        for (i in 0 until jsonArray.length()) {
            val prop = jsonArray.getJSONObject(i).getJSONObject("properties")
            // ID
            val id = prop.getString("GMID")

            // Facility Name
            val facName = prop.getString("Facility Name")

            // District
            val district = prop.getString("District")

            // Address
            val address = prop.getString("Address")

            // Opening Hours
            val openHour = prop.getString("Opening Hours")

            // Facility Details
            val details = prop.getString("Facility Details")

            val geodatas: MutableMap<String, Any> = HashMap()
            geodatas["ID"] = id
            geodatas["address"] = address
            geodatas["district"] = district
            geodatas["facility_name"] = facName
            geodatas["opening_hours"] = openHour
            geodatas["facility_details"] = details

            saveFireStore(geodatas)

        }
//        val intent = Intent(this, PersonListActivity::class.java).apply {
//            putStringArrayListExtra("data", studentList)
//        }
//        binding.jsonResultsRecyclerview.adapter = adapter
//        startActivity(intent)
    }

    // Function for storing cleaned formatted
    fun saveFireStore2(geodatas: MutableMap<String, Any>){
        val db = FirebaseFirestore.getInstance()

        db.collection("AllCourt")
            .document(geodatas["district"].toString())
            .set(hashMapOf(
                "district" to geodatas["district"],
            ))
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }

        db.collection("AllCourt")
            .document(geodatas["district"].toString())
            .collection("SportCentre")
            .add(geodatas)
            .addOnSuccessListener {
                Toast.makeText(this@GeodataActivity, "record ${it.id} added successfully", Toast.LENGTH_SHORT).show()

                // create courts based on court_no
                if (geodatas["courtno"].toString().isValidInt()){
                    var cno = Integer.parseInt(geodatas["courtno"].toString())

                    var minplayercount = 10000
                    for(i in 1 ..cno){
                        var rand_a = Random.nextInt(0, 12)
                        var rand_b = Random.nextInt(0, 12)
                        minplayercount = minOf(minplayercount,rand_a, rand_b)
                        db.collection("AllCourt")
                            .document(geodatas["district"].toString())
                            .collection("SportCentre")
                            .document(it.id)
                            .collection("Court")
                            .document(it.id + "_No"+ (i).toString())
                            .set(hashMapOf(
                                "name" to "Court #$i",
                                "address" to geodatas["address"],
                                "courtId" to it.id + "_No"+ (i).toString(),
                                "playercount_a" to rand_a,
                                "playercount_b" to rand_b
                            ))
                    }


                    // set the occupancy
                    db.collection("AllCourt")
                        .document(geodatas["district"].toString())
                        .collection("SportCentre")
                        .document(it.id)
                        .set(hashMapOf("occupancy" to minplayercount), SetOptions.merge())
                }
            }
            .addOnFailureListener {
                Toast.makeText(this@GeodataActivity, "record added unsuccessful", Toast.LENGTH_SHORT).show();
            }
    }

    fun String.isValidInt()
            = try {
        toInt().toString() == this
    } catch (x: NumberFormatException) {
        false
    }

    fun extractTime(input: String): Pair<String, String> {
        val timeRegex = Regex("""\b(\d{1,2}(?::\d{2})?\s*(?:a|p)\.?m\.?)\b""")
        val timeMatches = timeRegex.findAll(input).toList().map { it.value }
        val cleanedInput = input.replace("&ndash;", "-")
            .replace("&lt;br&gt;", "")
            .replace("No provision of floodlight", "")
            .replace("to", "-")
            .replace("daily", "")
            .replace("－", "-")
            .trim()

        return when {
            timeMatches.size >= 2 -> {
                val openTime = formatTime(timeMatches[0])
                val closeTime = formatTime(timeMatches[1])
                Pair(openTime, closeTime)
            }
            cleanedInput.contains("24 hours", ignoreCase = true) -> {
                Pair("00:00", "23:59")
            }
            cleanedInput.contains("closed", ignoreCase = true) -> {
                Pair("", "")
            }
            cleanedInput.matches("""\d{1,2}\s*a\.?m\.?\s*-+\s*\d{1,2}\s*p\.?m\.?\s*daily""".toRegex(RegexOption.IGNORE_CASE)) -> {
                val openTime = formatTime(cleanedInput.substringBefore("-").trim() + " am")
                val closeTime = formatTime(cleanedInput.substringAfter("-").substringBefore("daily").trim() + " pm")
                Pair(openTime, closeTime)
            }
            else -> {
                val timeRegex2 = Regex("""\b(\d{1,2}(?::\d{2})?\s*(?:a|p)\.?m\.?)\b\s*-+\s*\b(\d{1,2}(?::\d{2})?\s*(?:a|p)\.?m\.?)\b""")
                val timeMatches2 = timeRegex2.find(cleanedInput)?.groupValues ?: listOf("", "", "")
                val openTime = formatTime(timeMatches2[1])
                val closeTime = formatTime(timeMatches2[2])
                Pair(openTime, closeTime)
            }
        }
    }

    fun formatTime(time: String): String {
        val timeRegex = Regex("""\d{1,2}""")
        val hours = timeRegex.find(time)?.value?.toIntOrNull() ?: return ""
        val minutes = Regex("""(?<=:)\d{2}""").find(time)?.value?.toIntOrNull() ?: 0
        val amPm = if (time.contains("am", ignoreCase = true)) "am" else "pm"
        return if (hours == 12 && amPm == "am") {
            "00:${minutes.toString().padStart(2, '0')}"
        } else if (hours < 12 && amPm == "pm") {
            "${hours + 12}:${minutes.toString().padStart(2, '0')}"
        } else {
            "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}"
        }
    }

    fun extractCourtNumber(string: String): Int {
        return when {
            string.isEmpty() -> 1
            string.matches(Regex("\\d+")) -> string.toInt()
            else -> {
                val words = string.split(" ")
                for (word in words) {
                    if (word.matches(Regex("\\d+"))) {
                        return Math.round(word.toFloat())
                    }
                }
                1
            }
        }
    }










}
