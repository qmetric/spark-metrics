package com.qmetric.spark.metrics

import com.codahale.metrics.health.HealthCheck


class ResultModifier
{
    def static HealthCheck.Result newResult(final boolean isHealthy, final String message, final Throwable error)
    {

        def result = new HealthCheck.Result(isHealthy, message, error)
        return result
    }
}
