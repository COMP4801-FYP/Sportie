package hk.hkucs.sportieapplication.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
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
import hk.hkucs.sportieapplication.adapter.SportCentreAdapter
import hk.hkucs.sportieapplication.databinding.FragmentBookingStepOneBinding
import hk.hkucs.sportieapplication.models.BookingInformation
import hk.hkucs.sportieapplication.models.SportCentre

class BookingStep1Fragment:Fragment() {
    lateinit var dialog: AlertDialog
    private lateinit var binding: FragmentBookingStepOneBinding
    private lateinit var spinner:Spinner
    private lateinit var recycler_court:RecyclerView
    private lateinit var district_array:ArrayList<String>
    lateinit var searchview:SearchView
    lateinit var sportCentreArray:ArrayList<SportCentre>
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
//        return inflater.inflate(R.layout.fragment_booking_step_one, container, false)
        return binding.root
    }

    private fun filterList(newText: String?) {
        var filteredList = ArrayList<SportCentre>()
        for(item in sportCentreArray){
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
        FirebaseFirestore.getInstance().collection("AllCourt")
            .get()
            .addOnCompleteListener(object : OnCompleteListener<QuerySnapshot?> {
                override fun onComplete(task: Task<QuerySnapshot?>) {
                    if (task.isSuccessful) {
                        district_array = ArrayList()
                        district_array.add(ALL_DISTRICT)
                        sportCentreArray = ArrayList()

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
                                                    District_en = document.id,
                                                    Phone = doc.data["phone"].toString(),
                                                    Opening_hours_en = doc.data["opening_hours"].toString(),
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

                        var adapter = SportCentreAdapter(requireActivity(), Common.allCentreArray)
                        recycler_court.adapter = adapter
                        recycler_court.visibility = View.VISIBLE

                        spinner.adapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item, district_array)
                        spinner.onItemSelectedListener = object :
                            AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                                dialog.dismiss()
                                sportCentreArray = ArrayList()
                                loadBranchOfDistrict(district_array[position])
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

    private fun loadBranchOfDistrict(districtName: String) {
        dialog.show()
        Common.district = districtName

        if (districtName == ALL_DISTRICT){
            println("sizesx ${Common.allCentreArray.size}")
//            var tmpsportCentreArray = Common.allCentreArray.sortedWith(compareBy(SportCentre::Name_en))
//            sportCentreArray = Common.allCentreArray.sortedWith(compareBy(SportCentre::Name_en))
            sportCentreArray = Common.allCentreArray
            var adapter = SportCentreAdapter(requireActivity(), Common.allCentreArray)
            recycler_court.adapter = adapter
            recycler_court.visibility = View.VISIBLE
            dialog.dismiss()
        }
        else{

            FirebaseFirestore.getInstance().collection("AllCourt")
                .document(districtName)
                .collection("SportCentre")
                .get()
                .addOnSuccessListener{ documents ->
                    if (!documents.isEmpty) {
                        for (document in documents) {
                            sportCentreArray.add(
                                SportCentre(
                                    Name_en = document.data["facility_name"].toString(),
                                    Address_en = document.data["address"].toString(),
                                    court_id = document.id,
                                    District_en = document.id,
                                    Phone = document.data["phone"].toString(),
                                    Opening_hours_en = document.data["opening_hours"].toString(),))
                        }
                        sportCentreArray.sortWith(compareBy<SportCentre> { it.getName()})
                        var adapter = SportCentreAdapter(requireActivity(), sportCentreArray)
                        recycler_court.adapter = adapter
                        recycler_court.visibility = View.VISIBLE
                        dialog.dismiss()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(
                        activity?.javaClass?.simpleName,
                        "Error while loading court in district $districtName.", e
                    )
                    dialog.dismiss();
                }
        }
    }
}

