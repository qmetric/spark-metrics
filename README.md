#Spark Metrics

#####Purpose
A library of Route decorators to add metrics to spark based applications
and Routes to access the metrics

###Examples

####Add Ping
<i>/ping</i>
```
get(new PingRoute());
```
####Decorate Route
```
final String path = "/quote-engine/home/renewal";
post(new RouteMeterWrapper(path, metricRegistry, new RouteTimerWrapper(path, metricRegistry, new MyRoute(path))));
```

Here MyRoute is decorated with both a RouteMeterWrapper and a RouteTimerWrapper
####Add endpoint for metrics
This endpoint is mapped to <i>/metrics</i>
```
final MetricRegistry metricRegistry = new MetricRegistry();
get(new MetricsRoute(metricRegistry));
```
####Add a health check
```
HealthCheckRegistry healthCheckRegistry = new HealthCheckRegistry();
healthCheckRegistry.register("postcode-service", new HostHealthCheck(postcodeServiceUrl));
```

####Add endpoint for health check
This endpoint is mapped to /healthcheck

```
get(new HealthCheckRoute(healthCheckRegistry));
```

####An Alternative way to use
```
HealthCheckSetup.addHealthCheck("external-service", externalServiceUrl);
MetricSetup.timeAndMeterRoute(path, makeRoute(path), MetricSetup.Verb.POST);
```
This code will initialise a health check
It will decorate a route
Add both routes to spark
And add the metrics endpoint and the healthcheck endpoint to spark



