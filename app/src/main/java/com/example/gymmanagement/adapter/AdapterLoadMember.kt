package com.example.gymmanagement.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gymmanagement.R
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.gymmanagement.databinding.AllMemberListResBinding
import com.example.gymmanagement.model.AllMember

class AdapterLoadMember(val  arrayList: ArrayList<AllMember>): RecyclerView.Adapter<AdapterLoadMember.MyViewHolder>() {

    private var onClick: ((String) -> Unit)?=null
    fun onClick(onClick: ((String) -> Unit)){
        this.onClick = onClick
    }

    class MyViewHolder(val binding: AllMemberListResBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = AllMemberListResBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        with(holder){
            with(arrayList[position]){
                binding.txtAdapterName.text = this.firstName + " "+this.lastName
                binding.txtAdapterAge.text = "Age :" +this.age
                binding.txtAdapterWeight.text ="Weight : "+ this.weight
                binding.txtAdapterMobile.text = "Mobile : " +this.mobile
                binding.txtAddress.text = this.address
                binding.txtExpiry.text = "Expiry : " + this.expiryDate

                if (this.image.isNotEmpty()){
                    Glide.with(holder.itemView.context)
                        .load(this.image)
                        .into(binding.imgAdapterPic)
                }else{
                    if (this.gender == "Male"){
                        Glide.with(holder.itemView.context)
                            .load(R.drawable.boy)
                            .into(binding.imgAdapterPic)
                    }else{
                        Glide.with(holder.itemView.context)
                            .load(R.drawable.girl)
                            .into(binding.imgAdapterPic)
                    }
                }
                binding.layoutMemberList.setOnClickListener{
                    onClick?.invoke(this.id)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}