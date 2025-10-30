package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import javafx.fxml.FXML;
import javafx.scene.layout.Region;
import javafx.scene.web.WebView;

/**
 * A ui for the status bar that is displayed at the header of the application.
 */
public class ResultDisplay extends UiPart<Region> {

    private static final String FXML = "ResultDisplay.fxml";

    @FXML
    private WebView resultDisplay;

    private boolean isDarkTheme = true;
    private String lastMessage = "";

    /**
     * Creates a ResultDisplay with GitHub-style HTML formatting support.
     */
    public ResultDisplay() {
        super(FXML);
        resultDisplay.setContextMenuEnabled(false);
        initializeDisplay();
    }

    /**
     * Initializes the display with proper background color.
     */
    private void initializeDisplay() {
        String html = "<!DOCTYPE html><html><head><style>"
                + getGitHubStyleCss()
                + "</style></head><body></body></html>";
        resultDisplay.getEngine().loadContent(html);
    }

    /**
     * Sets the theme for the result display.
     * @param isDark true for dark theme, false for light theme
     */
    public void setTheme(boolean isDark) {
        this.isDarkTheme = isDark;
        if (!lastMessage.isEmpty()) {
            setFeedbackToUser(lastMessage);
        } else {
            initializeDisplay();
        }
    }

    /**
     * Sets the feedback message with GitHub-style rich HTML formatting.
     */
    public void setFeedbackToUser(String feedbackToUser) {
        requireNonNull(feedbackToUser);
        this.lastMessage = feedbackToUser;
        String html = convertToHtml(feedbackToUser);
        resultDisplay.getEngine().loadContent(html);
    }

    /**
     * Converts plain text message to GitHub-style HTML with rich formatting.
     */
    private String convertToHtml(String message) {
        boolean isError = isErrorMessage(message);
        boolean isSuccess = message.contains("✓");

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head>");
        html.append("<style>");
        html.append(getGitHubStyleCss());
        html.append("</style></head><body>");

        // Message container
        html.append("<div class='message-container ");
        html.append(isError ? "error" : (isSuccess ? "success" : "info"));
        html.append("'>");

        // Header with icon
        if (isError) {
            html.append("<div class='message-header error-header'>");
            html.append("<span class='icon'>✗</span>");
            html.append("<span class='header-text'>Error</span>");
            html.append("</div>");
        } else if (isSuccess) {
            html.append("<div class='message-header success-header'>");
            html.append("<span class='icon'>✓</span>");
            html.append("<span class='header-text'>Success</span>");
            html.append("</div>");
        }

        // Message body
        html.append("<div class='message-body'>");
        html.append(formatMessageContent(message));
        html.append("</div>");

        html.append("</div></body></html>");
        return html.toString();
    }

    /**
     * Formats the message content with proper HTML structure.
     */
    private String formatMessageContent(String message) {
        StringBuilder content = new StringBuilder();
        String[] lines = message.split("\n");

        for (String line : lines) {
            line = line.trim();

            if (line.isEmpty()) {
                continue;
            }

            // Handle list items (numbered, bullet, or bullet point)
            if (line.matches("^[\\d]+\\..*") || line.startsWith("-") || line.startsWith("*")) {
                // List item - ensure ul tag is open
                if (!content.toString().contains("<ul") || content.toString().endsWith("</ul>")) {
                    content.append("<ul class='bullet-list'>");
                }
                // Remove the list marker and add as list item
                String listContent = line.replaceFirst("^[-*\\d+.]\\s*", "");
                content.append("<li>").append(escapeHtml(listContent)).append("</li>");
            } else {
                // Close any open list before adding non-list content
                if (content.toString().endsWith("</li>")) {
                    content.append("</ul>");
                }

                String highlighted = highlightImportantParts(line);
                content.append("<p>").append(highlighted).append("</p>");
            }
        }

        // Close any open list
        if (content.toString().contains("<ul>") && !content.toString().endsWith("</ul>")) {
            content.append("</ul>");
        }

        return content.toString();
    }

    /**
     * Highlights important parts like student IDs, module codes, and numbers.
     */
    private String highlightImportantParts(String text) {
        text = escapeHtml(text);

        // Highlight student IDs (A0123456X format)
        text = text.replaceAll("(A\\d{7}[A-Z])", "<span class='student-id'>$1</span>");

        // Highlight module codes (CS2103T format)
        text = text.replaceAll("([A-Z]{2,3}\\d{4}[A-Z]?)", "<span class='module-code'>$1</span>");

        // Highlight tags in brackets
        text = text.replaceAll("\\[([^\\]]+)\\]", "<span class='tag'>[$1]</span>");

        return text;
    }

    /**
     * Escapes HTML special characters.
     */
    private String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    /**
     * Checks if the message indicates an error.
     */
    private boolean isErrorMessage(String message) {
        String lower = message.toLowerCase();
        return lower.contains("error")
                || lower.contains("invalid")
                || lower.contains("unknown")
                || lower.contains("missing")
                || lower.contains("not found")
                || lower.contains("cannot")
                || lower.contains("failed");
    }

    /**
     * Returns GitHub-style CSS for rich message formatting based on current theme.
     */
    private String getGitHubStyleCss() {
        if (isDarkTheme) {
            return getDarkThemeStyles();
        } else {
            return getLightThemeStyles();
        }
    }

    /**
     * Returns dark theme styles.
     */
    private String getDarkThemeStyles() {
        return "body { "
                + "margin: 0; "
                + "padding: 10px; "
                + "font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', "
                + "'Segoe UI', 'Noto Sans', Helvetica, Arial, sans-serif; "
                + "font-size: 15px; "
                + "line-height: 1.6; "
                + "color: rgba(255, 255, 255, 0.92); "
                + "background-color: rgba(28, 28, 30, 0.95); "
                + "} "
                + ".message-container { "
                + "border-radius: 12px; "
                + "border: 1px solid rgba(255, 255, 255, 0.12); "
                + "background: rgba(44, 44, 46, 0.7); "
                + "overflow: hidden; "
                + "box-shadow: 0 2px 12px rgba(0, 0, 0, 0.5); "
                + "} "
                + ".message-header { "
                + "padding: 12px 16px; "
                + "font-weight: 600; "
                + "font-size: 16px; "
                + "display: flex; "
                + "align-items: center; "
                + "gap: 8px; "
                + "border-bottom: 1px solid rgba(255, 255, 255, 0.08); "
                + "} "
                + ".success-header { "
                + "background: linear-gradient(135deg, #34C759 0%, #30D158 100%); "
                + "color: #ffffff; "
                + "} "
                + ".error-header { "
                + "background: linear-gradient(135deg, #FF453A 0%, #FF6961 100%); "
                + "color: #ffffff; "
                + "} "
                + ".icon { "
                + "font-size: 18px; "
                + "} "
                + ".message-body { "
                + "padding: 14px 16px; "
                + "} "
                + "p { "
                + "margin: 0 0 10px 0; "
                + "} "
                + "p:last-child { "
                + "margin-bottom: 0; "
                + "} "
                + ".bullet-list { "
                + "margin: 8px 0; "
                + "padding-left: 24px; "
                + "} "
                + ".bullet-list li { "
                + "margin: 6px 0; "
                + "} "
                + ".student-id { "
                + "font-weight: 600; "
                + "color: #0A84FF; "
                + "font-family: 'SF Mono', Monaco, 'Cascadia Code', Consolas, monospace; "
                + "font-size: 14px; "
                + "background: rgba(10, 132, 255, 0.2); "
                + "padding: 3px 6px; "
                + "border-radius: 4px; "
                + "} "
                + ".module-code { "
                + "font-weight: 600; "
                + "color: #BF5AF2; "
                + "font-family: 'SF Mono', Monaco, 'Cascadia Code', Consolas, monospace; "
                + "font-size: 14px; "
                + "background: rgba(191, 90, 242, 0.2); "
                + "padding: 3px 6px; "
                + "border-radius: 4px; "
                + "} "
                + ".number { "
                + "font-weight: 600; "
                + "color: #64D2FF; "
                + "} "
                + ".tag { "
                + "display: inline-block; "
                + "padding: 3px 9px; "
                + "margin: 0 4px; "
                + "font-size: 13px; "
                + "font-weight: 500; "
                + "background: rgba(10, 132, 255, 0.2); "
                + "color: #0A84FF; "
                + "border-radius: 12px; "
                + "border: 1px solid rgba(10, 132, 255, 0.4); "
                + "}";
    }

    /**
     * Returns light theme styles.
     */
    private String getLightThemeStyles() {
        return "body { "
                + "margin: 0; "
                + "padding: 10px; "
                + "font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', "
                + "'Segoe UI', 'Noto Sans', Helvetica, Arial, sans-serif; "
                + "font-size: 15px; "
                + "line-height: 1.6; "
                + "color: rgba(0, 0, 0, 0.87); "
                + "background-color: #f5f5f7; "
                + "} "
                + ".message-container { "
                + "border-radius: 12px; "
                + "border: 1px solid rgba(0, 0, 0, 0.12); "
                + "background: #ffffff; "
                + "overflow: hidden; "
                + "box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08); "
                + "} "
                + ".message-header { "
                + "padding: 12px 16px; "
                + "font-weight: 600; "
                + "font-size: 16px; "
                + "display: flex; "
                + "align-items: center; "
                + "gap: 8px; "
                + "border-bottom: 1px solid rgba(0, 0, 0, 0.08); "
                + "} "
                + ".success-header { "
                + "background: linear-gradient(135deg, #34C759 0%, #30D158 100%); "
                + "color: #ffffff; "
                + "} "
                + ".error-header { "
                + "background: linear-gradient(135deg, #FF3B30 0%, #FF6961 100%); "
                + "color: #ffffff; "
                + "} "
                + ".icon { "
                + "font-size: 18px; "
                + "} "
                + ".message-body { "
                + "padding: 14px 16px; "
                + "} "
                + "p { "
                + "margin: 0 0 10px 0; "
                + "} "
                + "p:last-child { "
                + "margin-bottom: 0; "
                + "} "
                + ".bullet-list { "
                + "margin: 8px 0; "
                + "padding-left: 24px; "
                + "} "
                + ".bullet-list li { "
                + "margin: 6px 0; "
                + "} "
                + ".student-id { "
                + "font-weight: 600; "
                + "color: #007AFF; "
                + "font-family: 'SF Mono', Monaco, 'Cascadia Code', Consolas, monospace; "
                + "font-size: 14px; "
                + "background: rgba(0, 122, 255, 0.12); "
                + "padding: 3px 6px; "
                + "border-radius: 4px; "
                + "} "
                + ".module-code { "
                + "font-weight: 600; "
                + "color: #AF52DE; "
                + "font-family: 'SF Mono', Monaco, 'Cascadia Code', Consolas, monospace; "
                + "font-size: 14px; "
                + "background: rgba(175, 82, 222, 0.12); "
                + "padding: 3px 6px; "
                + "border-radius: 4px; "
                + "} "
                + ".number { "
                + "font-weight: 600; "
                + "color: #5AC8FA; "
                + "} "
                + ".tag { "
                + "display: inline-block; "
                + "padding: 3px 9px; "
                + "margin: 0 4px; "
                + "font-size: 13px; "
                + "font-weight: 500; "
                + "background: rgba(0, 122, 255, 0.12); "
                + "color: #007AFF; "
                + "border-radius: 12px; "
                + "border: 1px solid rgba(0, 122, 255, 0.3); "
                + "}";
    }

}
