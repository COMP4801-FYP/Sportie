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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hk.hkucs.sportieapplication.R
import hk.hkucs.sportieapplication.models.SportCentre
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileReader
import java.io.IOException

//import com.example.parsejsonexample.Cell

class GeodataActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geodata)


        var btn_add = findViewById<Button>(R.id.btn_add)
        btn_add!!.setOnClickListener {
//            sendMessage()

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
                geodatas["ID"] = id
                geodatas["address"] = address
                geodatas["district"] = district
                geodatas["facility_name"] = facName
                geodatas["opening_hours"] = openHour
                geodatas["facility_details"] = details
                geodatas["phone"] = phone
                geodatas["latitude"] = lat
                geodatas["longitude"] = long
                geodatas["facilities"] = facilities
                geodatas["courtno"] = courtno

                saveFireStore2(geodatas)

            }
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
                    for(i in 1 ..cno){
                        db.collection("AllCourt")
                            .document(geodatas["district"].toString())
                            .collection("SportCentre")
                            .document(it.id)
                            .collection("Court")
                            .document(it.id + "_No"+ (i).toString())
                            .set(hashMapOf(
                                "name" to "Court #$i",
                                "address" to geodatas["address"],
                                "courtId" to it.id + "_No"+ (i).toString()
                            ))
                    }
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
}
