@import com.auo.juppy.ui.Page
@import gg.jte.Content


@param Page page
@param Content content
@param Content headContent = null

<!doctype html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="description" content="${page.description()}">
    <title>${page.title()}</title>

    @if(headContent != null)
        ${headContent}
    @endif
</head>
<body>
${content}
</body>
</html>
