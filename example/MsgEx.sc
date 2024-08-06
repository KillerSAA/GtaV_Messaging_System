SCRIPT_START
{
    LVAR_INT scplayer 
    LVAR_INT pTitle pSubtitle pText pText2 pRwTexture
    GET_PLAYER_CHAR 0 (scplayer)

    CONST_INT SPRITE_EXAMPLE 1

    WHILE TRUE 
        WAIT 10000 // avoid calling the function millions of times at the same time, it was not designed for that.

        IF IS_CHAR_IN_ANY_CAR scplayer 
            ALLOCATE_MEMORY 32 (pTitle)
            ALLOCATE_MEMORY 32 (pSubtitle)
            ALLOCATE_MEMORY 128 (pText)
            ALLOCATE_MEMORY 128 (pText2) // the text was divided into two strings due to limitations, if you need to use the second one, use ~n~ to skip the lines (otherwise the two texts will be on top of each other)

            // formatting..
            STRING_FORMAT (pTitle) "~r~Title" 
            STRING_FORMAT (pSubtitle) "~b~Subtitle"
            STRING_FORMAT (pText) "You are in a car"

            // get sprite
            LOAD_TEXTURE_DICTIONARY TEST 
            LOAD_SPRITE SPRITE_EXAMPLE sprite 
            GET_TEXTURE_FROM_SPRITE SPRITE_EXAMPLE (pRwTexture) // get the texture to send it as an argument
            CLEO_CALL ShowMessage 0 (/*Size*/280.0 /*RGBA*/0 0 255 220 /*time*/15000, /*Texts pointers*/pTitle, pSubtitle, pText, /*pText2*/pText2, /*1 = normal, 2 = people msg*/1, pRwTexture, /*spriteOffset*/6.0, 1.0 /*Size*/ -35.0 35.0)
            // sizeX must always be negative, otherwise its texture will be inverted.
        ENDIF
        
    ENDWHILE 
}
SCRIPT_END 

{  // use this function in your code //
    LVAR_FLOAT sizeX // In 
    LVAR_INT r g b a time pTitle pSubtitle pText pText2 size sprite // In 
    LVAR_FLOAT offX offY fSizeX sizeY // In
    LVAR_INT pScript 
    // CLEO_CALL ShowMessage 0 (/*Size*/280.0 /*RGBA*/0 0 255 220 /*time*/10000, /*Texts pointers*/pTitle, pSubtitle, pText, /*pText2*/pText2, /*1 = normal, 2 = people msg*/1, i, /*spriteOffset*/6.0, 1.0 /*Size*/ -35.0 35.0)
    ShowMessage:
        GET_SCRIPT_STRUCT_NAMED VMSGSYS (pScript)
        IF pScript = 0x0
            PRINT_FORMATTED_NOW "~r~[Error] script ~y~'Gtav_Msg_System.cs'~r~ does not exist!" 5000 
            CLEO_RETURN 0 ()
        ENDIF 

        SET_SCRIPT_VAR pScript 0 (1)
        SET_SCRIPT_VAR pScript 1 (sizeX)
        SET_SCRIPT_VAR pScript 2 (r)
        SET_SCRIPT_VAR pScript 3 (g) 
        SET_SCRIPT_VAR pScript 4 (b) 
        SET_SCRIPT_VAR pScript 5 (a)
        SET_SCRIPT_VAR pScript 6 (time)
        SET_SCRIPT_VAR pScript 7 (pTitle)
        SET_SCRIPT_VAR pScript 8 (pSubtitle)
        SET_SCRIPT_VAR pScript 9 (pText)
        SET_SCRIPT_VAR pScript 10 (pText2)
        SET_SCRIPT_VAR pScript 11 (size)
        SET_SCRIPT_VAR pScript 12 (sprite) // sprite texture
        SET_SCRIPT_VAR pScript 13 (offX)
        SET_SCRIPT_VAR pScript 14 (offY)
        SET_SCRIPT_VAR pScript 15 (fSizeX)
        SET_SCRIPT_VAR pScript 16 (sizeY)
    CLEO_RETURN 0 ()
}
