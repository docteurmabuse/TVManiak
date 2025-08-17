package com.tizzone.tvmaniak.core.designsystem.component

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import co.touchlab.kermit.Logger
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlHandler
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlParser

/**
 * Composable that displays HTML formatted text with rich formatting support.
 * Supports <b>, <strong>, <i>, <em>, <p>, <br>, <div> tags.
 */
@Composable
fun RichHtmlText(
    html: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    textAlign: TextAlign? = null,
    color: Color,
) {
    val annotatedText = html.parseHtmlToAnnotatedString()
    Text(
        text = annotatedText,
        modifier = modifier,
        style = style,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        textAlign = textAlign,
        color = color,
    )
}

/**
 * Extension function to parse HTML and convert to AnnotatedString with formatting.
 */
private fun String.parseHtmlToAnnotatedString(): AnnotatedString =
    try {
        buildAnnotatedString {
            val tagStack = mutableListOf<String>()

            val handler =
                KsoupHtmlHandler
                    .Builder()
                    .onOpenTag { name, _, _ ->
                        tagStack.add(name.lowercase())
                        when (name.lowercase()) {
                            "p" -> {
                                if (length > 0) append("\n\n")
                            }

                            "br" -> {
                                append("\n")
                            }

                            "div" -> {
                                if (length > 0) append("\n")
                            }
                        }
                    }.onCloseTag { name, _ ->
                        tagStack.removeLastOrNull()
                    }.onText { text ->
                        val currentTags = tagStack.toList()

                        val isBold = currentTags.any { it in listOf("b", "strong") }
                        val isItalic = currentTags.any { it in listOf("i", "em") }

                        if (isBold || isItalic) {
                            withStyle(
                                SpanStyle(
                                    fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                                    fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
                                ),
                            ) {
                                append(text)
                            }
                        } else {
                            append(text)
                        }
                    }.build()

            val parser = KsoupHtmlParser(handler = handler)
            parser.write(this@parseHtmlToAnnotatedString)
            parser.end()
        }
    } catch (e: Exception) {
        Logger.e(e) { "Error parsing HTML: ${this@parseHtmlToAnnotatedString}" }
        // Fallback à du texte simple nettoyé
        AnnotatedString(
            this
                .replace(Regex("<[^>]+>"), "")
                .replace("&nbsp;", " ")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&#39;", "'")
                .replace("&apos;", "'")
                .replace(Regex("\\s+"), " ")
                .trim(),
        )
    }
