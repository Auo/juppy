@import com.auo.juppy.ui.IndexPage

@param com.auo.juppy.ui.IndexPage indexPage

@tag.layout(
    page = indexPage,
    content = @`

    <ul>
     @for(var runner : indexPage.runners)
        <li>
            <a href="/runners/${runner.id.toString()}">${runner.uri.toString()}</a>
        </li>
     @endfor
     </ul>
    `
)