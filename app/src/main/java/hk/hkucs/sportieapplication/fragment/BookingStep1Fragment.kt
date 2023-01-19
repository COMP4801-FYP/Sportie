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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import dmax.dialog.SpotsDialog
import hk.hkucs.sportieapplication.Common.Common
import hk.hkucs.sportieapplication.Common.SpacesItemDecoration
import hk.hkucs.sportieapplication.adapter.SportCentreAdapter
import hk.hkucs.sportieapplication.databinding.FragmentBookingStepOneBinding
import hk.hkucs.sportieapplication.models.SportCentre

class BookingStep1Fragment:Fragment() {
    lateinit var dialog: AlertDialog
    private lateinit var binding: FragmentBookingStepOneBinding
    private lateinit var spinner:Spinner
    private lateinit var recycler_court:RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialog = SpotsDialog.Builder().setContext(activity).setCancelable(false).build()
    }

    private fun initView() {
        recycler_court.setHasFixedSize(true)
        recycler_court.layoutManager = GridLayoutManager(requireActivity(), 2)
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

        initView()
        loadAllCourt()
//        return inflater.inflate(R.layout.fragment_booking_step_one, container, false)
        return binding.root
    }

    private fun loadAllCourt() {
        FirebaseFirestore.getInstance().collection("AllCourt")
            .get()
            .addOnCompleteListener(object : OnCompleteListener<QuerySnapshot?> {
                override fun onComplete(task: Task<QuerySnapshot?>) {
                    if (task.isSuccessful) {
                        var districy_array = ArrayList<String>()
                        districy_array.add("Please choose district")

                        for (document in task.result!!) {
                            districy_array.add(document.id)
                        }
                        spinner.adapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item, districy_array)
                        spinner.onItemSelectedListener = object :
                            AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                                if(position>0){
                                    loadBranchOfDistrict(districy_array[position].toString())
                                }
                                else{
                                    recycler_court.visibility = View.GONE
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                TODO("Not yet implemented")
                            }
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.exception)
                    }
                }
            })
//            .addOnFailureListener { e ->
//                Log.e(
//                    activity?.javaClass?.simpleName, "Error while loading sport court.", e
//                )
//            }
    }

    private fun loadBranchOfDistrict(districtName: String) {
        dialog.show()

        Common.district = districtName

        FirebaseFirestore.getInstance().collection("AllCourt")
            .document(districtName)
            .collection("SportCentre")
            .get()
            .addOnSuccessListener{ documents ->
                if (!documents.isEmpty) {
                    var sportCentreArray = ArrayList<SportCentre>()
                    for (document in documents) {
                        sportCentreArray.add(SportCentre(Name_en = document.data["facility_name"].toString(), Address_en = document.data["address"].toString(), court_id = document.id))
                    }
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

