package com.example.truck_weight_example

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.here.android.mpa.common.GeoCoordinate
import com.here.android.mpa.common.OnEngineInitListener
import com.here.android.mpa.mapping.AndroidXMapFragment
import com.here.android.mpa.mapping.Map
import com.here.android.mpa.mapping.MapCircle
import com.here.android.mpa.mapping.MapRoute
import com.here.android.mpa.routing.CoreRouter
import com.here.android.mpa.routing.RouteOptions
import com.here.android.mpa.routing.RoutePlan
import com.here.android.mpa.routing.RouteResult
import com.here.android.mpa.routing.RouteWaypoint
import com.here.android.mpa.routing.Router
import com.here.android.mpa.routing.RoutingError
import java.util.EnumSet

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private var map: Map? = null

    private val mapView: AndroidXMapFragment
        get() = supportFragmentManager.findFragmentById(R.id.mapFragment) as AndroidXMapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    private fun initialize() {
        mapView.init { error ->
            if (error == OnEngineInitListener.Error.NONE) {
                map = mapView.map
                map?.setCenter(GeoCoordinate(50.231214, 18.9887583), Map.Animation.NONE)
                map?.zoomLevel = 16.0
                map?.fleetFeaturesVisible = EnumSet.of(Map.FleetFeature.TRUCK_RESTRICTIONS)
                showMarkers()
            } else {
                Toast.makeText(
                    applicationContext,
                    "Error initializing map: $error",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        findViewById<Button>(R.id.routeA).setOnClickListener {
            removeRoutes()
            // Michałkowice -> Katowice Debowa
            createRoute(
                startPoint = GeoCoordinate(50.319609, 18.9952443),
                destination = GeoCoordinate(50.2747916,18.9979625)
            )
            map?.setCenter(GeoCoordinate(50.274741,18.9970029), Map.Animation.NONE)
            map?.zoomLevel = 18.0
        }

        findViewById<Button>(R.id.routeB).setOnClickListener {
            removeRoutes()
            // Bielsko Biala -> Katowice
            createRoute(
                startPoint = GeoCoordinate(49.812179, 18.9672229),
                destination = GeoCoordinate(50.230363, 18.9939613)
            )
            map?.setCenter(GeoCoordinate(50.2322981, 18.9973566), Map.Animation.NONE)
            map?.zoomLevel = 16.0
        }
    }

    private fun createRoute(destination: GeoCoordinate, startPoint: GeoCoordinate) {
        val coreRouter = CoreRouter()
        val routePlan = RoutePlan()

        val routeOptions = RouteOptions()
        routeOptions.transportMode = RouteOptions.TransportMode.TRUCK
        routeOptions.routeType = RouteOptions.Type.FASTEST
        routeOptions.setFerriesAllowed(false)
        routeOptions.setTollRoadsAllowed(false)
        routeOptions.setHighwaysAllowed(false)


        routeOptions.truckWeightPerAxle = 6.0f
        routeOptions.truckTrailersCount = 2
        routeOptions.truckLimitedWeight = 20.0f

        routeOptions.routeCount = 1

        routePlan.routeOptions = routeOptions

        routePlan.addWaypoint(RouteWaypoint(startPoint))
        routePlan.addWaypoint(RouteWaypoint(destination))

        coreRouter.calculateRoute(
            routePlan,
            object : Router.Listener<List<RouteResult>, RoutingError> {
                override fun onProgress(i: Int) {}

                override fun onCalculateRouteFinished(
                    routeResults: List<RouteResult>,
                    routingError: RoutingError
                ) {
                    if (routingError == RoutingError.NONE) {
                        routeResults.forEach { route ->
                            Log.w("weight example", "Result problems: ${route.violatedOptions}")
                            map?.addMapObject(MapRoute(route.route))
                        }
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Routing error: $routingError",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        )
    }

    private fun showMarkers() {
        val currentPositionMarker = MapCircle(20.0, GeoCoordinate(50.515798, 22.141218)).apply {
            this.fillColor = Color.parseColor("#4444EE")
            this.lineColor = Color.parseColor("#9999EE")
            this.lineWidth = 5
        }
        val routeAMarker = MapCircle(20.0, GeoCoordinate(50.2747916,18.9979625)).apply {
            this.fillColor = Color.parseColor("#CC9999")
            this.lineColor = Color.parseColor("#AA6666")
            this.lineWidth = 5
        }
        val routeBMarker = MapCircle(20.0, GeoCoordinate(50.230363, 18.9939613)).apply {
            this.fillColor = Color.parseColor("#99CC99")
            this.lineColor = Color.parseColor("#66AA66")
            this.lineWidth = 5
        }

        map?.addMapObject(currentPositionMarker)
        map?.addMapObject(routeAMarker)
        map?.addMapObject(routeBMarker)
    }

    private fun removeRoutes() {
        map?.removeAllMapObjects()
        showMarkers()
    }
}
