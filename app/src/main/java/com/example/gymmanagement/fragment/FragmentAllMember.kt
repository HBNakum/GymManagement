package com.example.gymmanagement.fragment

import android.R.attr.fragment
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gymmanagement.R
import com.example.gymmanagement.adapter.AdapterLoadMember
import com.example.gymmanagement.databinding.FragmentAllMemberBinding
import com.example.gymmanagement.global.DB
import com.example.gymmanagement.global.MyFunction
import com.example.gymmanagement.model.AllMember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FragmentAllMember : BaseFragment() {

    private  val  TAG = "FragmentAllMember"
    var db: DB? = null
    var adapter: AdapterLoadMember?=null
    var arrayList: ArrayList<AllMember> = ArrayList()

    private lateinit var binding: FragmentAllMemberBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentAllMemberBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = activity?.let { DB(it) }

        binding.rdGroupMember.setOnCheckedChangeListener{radioGroup , i->
            when(id){
                R.id.rdActiveMember -> {

                }
                R.id.rdInActiveMember -> {

                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadData("A")
    }

    fun <R> CoroutineScope.executeAsyncTask(
        onPreExecute: () -> Unit,
        doInBackground: () -> R,
        onPostExecute: (R) -> Unit
    ) = launch{
        onPreExecute()
        val result = withContext(Dispatchers.IO){
            doInBackground()
        }
        onPostExecute(result)

    }

    private fun loadData(memberStatus: String){
        lifecycleScope.executeAsyncTask(onPreExecute = {

            showDialog("Processing...")
        }, doInBackground = {

            val sqlQuery = "SELECT * FROM MEMBER WHERE STATUS = '"+memberStatus+"'"
            db?.fireQuery(sqlQuery)?.use {
                if (it.count>0){
                    it.moveToFirst()
                    do {
                        val list = AllMember(id = MyFunction.getvalue(it,"ID"),
                            firstName = MyFunction.getvalue(it,"FIRST_NAME"),
                            lastName = MyFunction.getvalue(it,"LAST_NAME"),
                            age = MyFunction.getvalue(it,"AGE"),
                            gender = MyFunction.getvalue(it,"GENDER"),
                            weight =  MyFunction.getvalue(it,"WEIGHT"),
                            mobile = MyFunction.getvalue(it,"MOBILE"),
                            address = MyFunction.getvalue(it,"ADDRESS"),
                            image = MyFunction.getvalue(it,"IMAGE_PATH"),
                            dateOfJoining = MyFunction.returnUserDateFormat(MyFunction.getvalue(it,"DATE_OF_JOINING")),
                            expiryDate = MyFunction.returnUserDateFormat(MyFunction.getvalue(it,"EXPIRE_ON")))

                        arrayList.add(list)
                    }while (it.moveToNext())

                }
            }
        }, onPostExecute = {
            if (arrayList.size>0){
                binding.recyclerViewMember.visibility = View.VISIBLE
                binding.txtAllMemberNDF.visibility = View.GONE

                adapter = AdapterLoadMember(arrayList)
                binding.recyclerViewMember.layoutManager = LinearLayoutManager(activity)
                binding.recyclerViewMember.adapter = adapter


                adapter?.onClick {
                    loadFragment(it)
                }

            }else{
                binding.recyclerViewMember.visibility = View.GONE
                binding.txtAllMemberNDF.visibility = View.VISIBLE
            }
            CloseDialog()
        })
    }

    private fun loadFragment(id: String){
        val fragment = FragmentAddMember()
        val args = Bundle()
        args.putString("ID",id)
        fragment.arguments = args
        val fragmentManager: FragmentManager ?=fragmentManager
        fragmentManager!!.beginTransaction().replace(R.id.frame_container,fragment,"FragmentAdd").commit()
    }


}