package io.github.mindjet.liteweather.view

import android.os.Bundle
import io.github.mindjet.liteweather.R
import io.github.mindjet.liteweather.adapter.CommonAdapter
import io.github.mindjet.liteweather.model.City
import io.github.mindjet.liteweather.util.CityHelper
import io.github.mindjet.liteweather.util.setVerticalLinear
import kotlinx.android.synthetic.main.include_recycler_view.*
import kotlinx.android.synthetic.main.item_city_management.view.*

class CityManagementActivity : BaseAppCompatActivity() {

    private lateinit var adapter: CommonAdapter<City>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_management)
        supportActionBar?.title = "城市列表管理"

        adapter = CommonAdapter(
            onItemBound = { model, vh, _ ->
                vh.itemView.apply {
                    tvCity.text = model.name
                    tvCitySymbol.text = model.name[0].toString()
                }
            },
            layoutId = R.layout.item_city_management
        )
        recyclerView.setVerticalLinear(this)
        recyclerView.adapter = adapter

        adapter.addAll(CityHelper.getPinnedCities(this)!!)
    }

}