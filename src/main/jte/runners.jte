@import com.auo.juppy.ui.RunnerPage;
@import java.time.Duration;
@import java.time.Instant;
@import java.util.Collections;
@import java.util.Date;
@import java.util.stream.Collector;
@import java.util.stream.Collectors

@param com.auo.juppy.ui.RunnerPage runnerPage

@tag.layout(
    page = runnerPage,
    content = @`
        <h1>${runnerPage.config.uri.toString()}</h1>
        <p>Created: ${runnerPage.config.created}</p>
        <p>Interval: ${runnerPage.config.interval} ms</p>
        <p>Timeout: ${runnerPage.config.timeout} ms</p>

        <canvas id="response-time-chart" width="500" height="400"></canvas>

    `,
headContent = @`
    <script src="https://cdn.jsdelivr.net/npm/chart.js@2.9.4/dist/Chart.min.js"></script>
    <script>

        window.addEventListener('DOMContentLoaded', (event) => {
                    var data = [];
                    var ctx = document.getElementById('response-time-chart').getContext('2d');

                   var responseTimeChart = new Chart(ctx, {
                       type: 'line',
                       data: {
                                 labels: $unsafe{runnerPage.labels},
                                 datasets: [{
                                     data: $unsafe{runnerPage.data},
                                     label: "Response time (ms)",
                                     borderColor: "#3e95cd",
                                     fill: false,
                                     lineTension: 0.1
                                   }]
                               },
                       options: {
                        responsive: true
                       }
                   });
        });
    </script>
    `
)