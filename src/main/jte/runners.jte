@import com.auo.juppy.ui.RunnerPage

@param com.auo.juppy.ui.RunnerPage runnerPage

@tag.layout(
    page = runnerPage,
    content = @`
        <p>${runnerPage.config.toString()}</p>

        @for(var result : runnerPage.result)
            <p>${result.toString()}</p>
        @endfor
    `
)