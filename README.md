Steps to run:
1. setup correct application id in build.gradle (to match license keys)
2. setup license keys in AndroidManifest.xml
3. copy your sdk file (aar) to app/libs/
4. run on real device

To calculate example routes hit Route A or Route B button and check results.
Route A destination is a red circle
Route B destination is a green circle

According to weight limits for routes in that area both routes should be forbidden, but for some reason map engine returns Route A calculated through 5tons and 3.5 tons weight limit roads.

Default settings:
```kotlin
        val routeOptions = RouteOptions()
        routeOptions.transportMode = RouteOptions.TransportMode.TRUCK
        routeOptions.routeType = RouteOptions.Type.FASTEST

        routeOptions.truckWeightPerAxle = 6.0f
        routeOptions.truckTrailersCount = 2
        routeOptions.truckLimitedWeight = 20.0f

        routeOptions.routeCount = 1
```
Change truck settings in MainActivity.kt:66
