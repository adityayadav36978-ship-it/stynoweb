package com.example.ui.components

import com.example.data.model.Property
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

/**
 * Cluster item representing a Property listing for Google Maps marker clustering.
 *
 * Implements [ClusterItem] to enable Google Maps marker clustering in high-density areas.
 * Holds reference to the full [Property] model for fast access to pricing, ratings, and details.
 */
data class PropertyClusterItem(
    val property: Property
) : ClusterItem {
    override fun getPosition(): LatLng = LatLng(property.latitude, property.longitude)
    override fun getTitle(): String = property.name
    override fun getSnippet(): String = "₹${property.startingPrice.toInt()} • ${property.propertyType.displayName}"
    override fun getZIndex(): Float? = null
}
