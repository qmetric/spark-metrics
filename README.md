Spark Metrics
====================

[![Build Status](https://travis-ci.org/qmetric/spark-metrics.png)](https://travis-ci.org/qmetric/spark-metrics)

## Purpose

A library of Route decorators to add metrics to spark based applications
and Routes to access the metrics

## Examples

### Add Ping

Url : <i>/ping</i>

```java
get(new PingRoute());
```
### Decorate Route

```java
final String path = "/path";
post(new RouteMeterWrapper(path, metricRegistry, new RouteTimerWrapper(path, metricRegistry, new MyRoute(path))));
```

Here MyRoute is decorated with both a RouteMeterWrapper and a RouteTimerWrapper

### Add endpoint for metrics

This endpoint is mapped to <i>/metrics</i>

```java
final MetricRegistry metricRegistry = new MetricRegistry();
get(new MetricsRoute(metricRegistry));
```

### Add a health check

```java
HealthCheckRegistry healthCheckRegistry = new HealthCheckRegistry();
healthCheckRegistry.register("external-service", new HostHealthCheck(externalServiceUrl));
```

### Add endpoint for health check

This endpoint is mapped to /healthcheck

```java
get(new HealthCheckRoute(healthCheckRegistry));
```

### An Alternative way to use

```java
HealthCheckSetup.addHealthCheck("external-service", externalServiceUrl);
MetricSetup.timeAndMeterRoute(path, makeRoute(path), MetricSetup.Verb.POST);
```

This code will initialise a health check
It will decorate a route
Add both routes to spark
And add the metrics endpoint and the healthcheck endpoint to spark


## Licence
Copyright 2013 Qmetric Ltd

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
