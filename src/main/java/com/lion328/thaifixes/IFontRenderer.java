package com.lion328.thaifixes;

public interface IFontRenderer {

    /**
     * Set FontRendererWrapper object.
     *
     * @param wrapper FontRendererWrapper object.
     */
    void setFontRendererWrapper(FontRendererWrapper wrapper);

    /**
     * Check for supported character.
     *
     * @param c Character.
     * @return Return true if support.
     */
    boolean isSupportedCharacter(char c);

    /**
     * Render a character.
     *
     * @param c      Character to render.
     * @param italic Italic style.
     * @return Return character width.
     */
    float renderCharacter(char c, boolean italic);

    /**
     * Get character width.
     *
     * @param c Character.
     * @return Width of character.
     */
    int getCharacterWidth(char c);
}
