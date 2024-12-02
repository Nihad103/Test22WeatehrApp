package com.example.test22weatehrapp.fragments.location

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Adapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.test22weatehrapp.data.RemoteLocation
import com.example.test22weatehrapp.databinding.ItemContainerLocationBinding

class LocationsAdapter(
    private val onLocationClicked: (RemoteLocation) -> Unit
) : RecyclerView.Adapter<LocationsAdapter.LocationViewHolder>(){

    private val locations = mutableListOf<RemoteLocation>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<RemoteLocation>) {
        locations.clear()
        locations.addAll(data)
        notifyDataSetChanged()
    }

    inner class LocationViewHolder(
        private val binding: ItemContainerLocationBinding
    ) : ViewHolder(binding.root) {
        fun bind(remoteLocation: RemoteLocation) {
            with(remoteLocation) {
                val location = "$name, $region, $country"
                binding.textRemoteLocation.text = location
                binding.root.setOnClickListener { onLocationClicked(remoteLocation) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        return LocationViewHolder(
            ItemContainerLocationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return locations.size
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(remoteLocation = locations[position])
    }
}