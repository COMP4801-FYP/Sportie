package hk.hkucs.sportieapplication.fragment

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import dmax.dialog.SpotsDialog
import hk.hkucs.sportieapplication.Common.Common
import hk.hkucs.sportieapplication.Common.Common.Companion.ALL_DISTRICT
import hk.hkucs.sportieapplication.Common.SpacesItemDecoration
import hk.hkucs.sportieapplication.adapter.SorterAdapter
import hk.hkucs.sportieapplication.adapter.SportCentreAdapter
import hk.hkucs.sportieapplication.databinding.FragmentBookingStepOneBinding
import hk.hkucs.sportieapplication.models.SportCentre
import kotlin.math.roundToInt

class BookingStep1Fragment:Fragment() {
    lateinit var dialog: AlertDialog
    private lateinit var binding: FragmentBookingStepOneBinding

    private lateinit var spinner:Spinner
    private lateinit var sorter:Spinner

    private lateinit var recycler_court:RecyclerView
    private lateinit var district_array:ArrayList<String>

    lateinit var searchview:SearchView
//    lateinit var sportCentreArray:ArrayList<SportCentre>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialog = SpotsDialog.Builder().setContext(activity).setCancelable(false).build()
    }

    private fun initView() {
        recycler_court.setHasFixedSize(true)
        recycler_court.layoutManager = GridLayoutManager(requireActivity(), 1)
        recycler_court.addItemDecoration(SpacesItemDecoration(4))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBookingStepOneBinding.inflate(layoutInflater)
        spinner = binding.spinner
        recycler_court = binding.recyclerCourt

        sorter = binding.spinnerSort

        searchview = binding.searchview
        searchview.clearFocus()
        searchview.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    filterList(newText)
                    return true
                }

            })

        initView()
        loadAllCourt()

        return binding.root
    }

    private fun filterList(newText: String?) {
//        var sportCtrArray = Common.allCentreArray
        var sportCtrArray = Common.tmpCentreForFilter
        var filteredList = ArrayList<SportCentre>()

        for(item in sportCtrArray){
            if(item.getName().toLowerCase().contains(newText!!.toLowerCase())){
                filteredList.add(item)
            }
        }

        if (filteredList.isEmpty()){
            recycler_court.visibility = View.GONE
        }
        else{
            filteredList.sortWith(compareBy<SportCentre> { it.getName()})
            var adapter = SportCentreAdapter(requireActivity(), filteredList)
            recycler_court.adapter = adapter
            recycler_court.visibility = View.VISIBLE
            dialog.dismiss()
        }
    }

    private fun loadAllCourt() {
        dialog.show()
        if (Common.allCentreArray.isEmpty()){
            FirebaseFirestore.getInstance().collection("AllCourt")
                .get()
                .addOnCompleteListener(object : OnCompleteListener<QuerySnapshot?> {
                    override fun onComplete(task: Task<QuerySnapshot?>) {
                        if (task.isSuccessful) {
                            district_array = ArrayList()
                            district_array.add(ALL_DISTRICT)

                            for (document in task.result!!) {
                                district_array.add(document.id)

                                FirebaseFirestore.getInstance().collection("AllCourt")
                                    .document(document.id)
                                    .collection("SportCentre")
                                    .get()
                                    .addOnSuccessListener { documents ->
                                        if (!documents.isEmpty) {
                                            for (doc in documents) {
                                                Common.allCentreArray.add(
                                                    SportCentre(
                                                        Name_en = doc.data["facility_name"].toString(),
                                                        Address_en = doc.data["address"].toString(),
                                                        court_id = doc.id,
                                                        District_en = doc.data["district"].toString(),
                                                        Phone = doc.data["phone"].toString(),
                                                        Opening_hours_en = doc.data["opening_hours"].toString(),
                                                        Ancillary_facilities_en = doc.data["facilities"].toString(),
                                                        Latitude = doc.data["latitude"].toString(),
                                                        Longitude = doc.data["longitude"].toString(),
                                                        Bookmarks = doc.data["bookmarks"].toString().toInt(),
                                                        Occupancy = doc.data["occupancy"].toString().toInt(),
                                                        CurDistance = calculate_distance(doc.data["latitude"].toString(),doc.data["longitude"].toString())
                                                    )
                                                )
                                            }
                                            Common.allCentreArray.sortWith(compareBy<SportCentre> { it.getName()})
                                        } else {
                                            Log.e(
                                                activity?.javaClass?.simpleName,
                                                "Error while loading court in district ${document.id}."
                                            )
                                            dialog.dismiss();
                                        }
                                    }
                            }

                            Thread.sleep(2000)
                            

                            Common.allDistrictArray = district_array
                            Common.tmpCentreForFilter = Common.allCentreArray

                            var adapter = SportCentreAdapter(requireActivity(), Common.allCentreArray)
                            recycler_court.adapter = adapter
                            recycler_court.visibility = View.VISIBLE

                            spinner.adapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item, Common.allDistrictArray)
                            spinner.onItemSelectedListener = object :
                                AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                                    dialog.dismiss()
                                    loadBranchOfDistrict(district_array[position])
                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                    TODO("Not yet implemented")
                                }
                            }

                            sorter.adapter = SorterAdapter(requireContext(), Common.getsorterlist())
                            sorter.onItemSelectedListener = object :
                                AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                                    dialog.dismiss()
                                    loadBranchOfDistrict(Common.district!!)
                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                    TODO("Not yet implemented")
                                }
                            }


                        } else {
                            Log.d(TAG, "Error getting documents: ", task.exception)
                            dialog.dismiss()
                        }
                    }
                })
        }
        else{
            var adapter = SportCentreAdapter(requireActivity(), Common.allCentreArray)
            recycler_court.adapter = adapter
            recycler_court.visibility = View.VISIBLE

            spinner.adapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item, Common.allDistrictArray)
            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    dialog.dismiss()
                    loadBranchOfDistrict(Common.allDistrictArray[position])
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }

            sorter.adapter = SorterAdapter(requireContext(), Common.getsorterlist())
            sorter.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    dialog.dismiss()
                    loadBranchOfDistrict(Common.district!!)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }


        }

    }

    private fun loadBranchOfDistrict(districtName: String) {
        dialog.show()
        Common.district = districtName

        var sportCtrArray = Common.allCentreArray

        var sortertype = Common.getsorterlist()[binding.spinnerSort.selectedItemPosition].getName()

        var sortedSportCentreArray = Common.allCentreArray as List<SportCentre>

        if (sortertype == "A-Z"){
            sortedSportCentreArray = sportCtrArray.sortedWith(compareBy<SportCentre> {
                it.Name_en
            })
        }
        else if (sortertype == "Bookmark"){
            // in descending order
            sortedSportCentreArray = sportCtrArray.sortedWith(compareByDescending<SportCentre> {
                it.Bookmarks
            })
        }
        else if (sortertype == "Occupancy"){
            // in descending order
            sortedSportCentreArray = sportCtrArray.sortedWith(compareBy<SportCentre> {
                it.Occupancy
            })
            sortedSportCentreArray = sortedSportCentreArray.filter { it.Occupancy < 4}
        }

        else if (sortertype == "Distance"){
            // in ascending order
            sortedSportCentreArray = sportCtrArray.sortedWith(compareBy<SportCentre> {
                it.CurDistance
            })
        }

        // filter based on district
        val filteredSportCentreArray: List<SportCentre> = if (districtName != ALL_DISTRICT) {
            sortedSportCentreArray.filter {
                it.District_en == districtName
            }
        } else {
            sortedSportCentreArray
        }

        Common.tmpCentreForFilter = ArrayList(filteredSportCentreArray)

        var adapter = SportCentreAdapter(requireActivity(), ArrayList(filteredSportCentreArray))
        recycler_court.adapter = adapter
        recycler_court.visibility = View.VISIBLE
        dialog.dismiss()
    }


    private fun loadByBookmarks(districtName: String){
        dialog.show()
        Common.district = districtName

        var sportCtrArray = Common.allCentreArray

        // in descending order
        val sortedSportCentreArray = sportCtrArray.sortedWith(compareByDescending<SportCentre> {
            it.Bookmarks
        })

        val filteredSportCentreArray: List<SportCentre> = if (districtName != ALL_DISTRICT) {
            sortedSportCentreArray.filter {
                it.District_en == districtName
            }
        } else {
            sortedSportCentreArray
        }

        var adapter = SportCentreAdapter(requireActivity(), ArrayList(filteredSportCentreArray))
        recycler_court.adapter = adapter
        recycler_court.visibility = View.VISIBLE
        dialog.dismiss()


    }

    fun calculate_distance(latitude:String, longitude: String): Double{
        // calculate distance and set distance
        var lat1 = Common.dmsToDd(latitude)
        var long1 = Common.dmsToDd(longitude)
        var distance = (Common.distance(lat1,long1, Common.curlatitude, Common.curlongitude)*10.0).roundToInt() /10.0
        return distance
    }

}

