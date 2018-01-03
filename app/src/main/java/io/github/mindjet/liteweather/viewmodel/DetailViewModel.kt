package io.github.mindjet.liteweather.viewmodel

import android.databinding.ObservableField
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.v7.widget.LinearLayoutManager
import io.github.mindjet.library.extension.log
import io.github.mindjet.liteweather.R
import io.github.mindjet.liteweather.consant.Constant
import io.github.mindjet.liteweather.consant.WeatherTxt
import io.github.mindjet.liteweather.databinding.ActivityDetailBinding
import io.github.mindjet.liteweather.helper.toast
import io.github.mindjet.liteweather.model.DetailResponse
import io.github.mindjet.liteweather.network.CommTrans
import io.github.mindjet.liteweather.network.RetrofitInstance
import io.github.mindjet.liteweather.network.WeatherService
import io.github.mindjet.liteweather.recycler_view.ListAdapter
import io.github.mindjet.livemvvm.viewmodel.BaseItemViewModel
import io.github.mindjet.livemvvm.viewmodel.BaseViewModel

/**
 * ViewModel for weather detail.
 *
 * Created by Mindjet on 2017/11/11.
 */
class DetailViewModel : BaseViewModel<ActivityDetailBinding>() {

    private val city by lazy { activity.intent.getStringExtra(Constant.INTENT_CITYNAME) }
    private val condition by lazy { activity.intent.getStringExtra(Constant.INTENT_CONDITION) }
    private val temperature by lazy { activity.intent.getStringExtra(Constant.INTENT_TEMPERATURE) }

    private val recyclerView by lazy { binding.recyclerView }
    private val collapsingLayout by lazy { binding.collapsingLayout }
    private val toolbar by lazy { binding.toolbar }

    private var response: DetailResponse? = null

    private val adapter by lazy { ListAdapter<BaseItemViewModel<*>>() }

    val cityOb by lazy { ObservableField("--") }
    val tempOb by lazy { ObservableField("--") }
    val conditionOb by lazy { ObservableField("--") }
    val windOb by lazy { ObservableField("--") }
    val humidityOb by lazy { ObservableField("--") }
    val background by lazy { ObservableField<Drawable>() }

    override fun onAttached(binding: ActivityDetailBinding) {
        cityOb.set(city)
        tempOb.set(temperature)
        conditionOb.set(condition)
        initRecyclerView()
        initCollapsingLayout()
        updateBackground()
        fetchData()
    }

    private fun initCollapsingLayout() {
        collapsingLayout?.setCollapsedTitleTextAppearance(R.style.TextAppearance)
        collapsingLayout?.setExpandedTitleTextAppearance(R.style.TextAppearance_Expend)
        toolbar?.setTitleTextColor(Color.WHITE)
        toolbar?.setNavigationOnClickListener { onBack() }
    }

    private fun initRecyclerView() {
        recyclerView?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView?.adapter = adapter
    }

    private fun fetchData() {
        RetrofitInstance.get<WeatherService>()
                .getDetailWeather(city!!)
                .compose(CommTrans.asyncSync<DetailResponse>())
                .subscribe({
                    response = it
                    updateNowBlock()
                    updateDailyBlock()
                }, { log(it) })
    }

    private fun updateBackground() {
        val drawableId = WeatherTxt.getCorrespondingBackground(condition!!)
        background.set(context.resources?.getDrawable(drawableId))
    }

    private fun updateNowBlock() {
        tempOb.set(response?.now?.temperature)
        conditionOb.set(response?.now?.conditionTxt)
        windOb.set("${response?.now?.windScale}/${response?.now?.windDirection}")
        humidityOb.set("湿度${response?.now?.humidity}%")
    }

    private fun updateDailyBlock() {
        adapter.add(UpcomingDailyViewModel(response?.dailyForecast))
        adapter.notifyItemInserted(0)
        toast(adapter.size.toString())
    }

    fun onBack() {
        activity.onBackPressed()
    }

}