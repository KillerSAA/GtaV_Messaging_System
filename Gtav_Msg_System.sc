SCRIPT_START
{
    LVAR_INT bCallFunc // In
    LVAR_FLOAT sizeX // In 
    LVAR_INT r g b a time pTitle pSubtitle pText pText2 iType sprite// In 
    LVAR_FLOAT offX offY fSizeX fSizeY // In
    LVAR_INT j pLabel list font_list pList

    SCRIPT_NAME VMSGSYS

    // Box state
    CONST_INT FIRST_BOX_CREATED 1 
    CONST_INT LAST_BOX_CREATED 2 
    CONST_INT NEW_BOX_CREATED 3 

    // Box types 
    CONST_INT NORMAL_MESSAGE_BOX 1 // a normal text box, and you can add a sprite of your choice
    CONST_INT PEOPLE_MESSAGE_BOX 2 // a text box dedicated to messages received by cell phone

    // sprites 
    CONST_INT SPRITE_NOTF 1
    CONST_INT SPRITE_MSG_ICON 3

    CREATE_LIST DATATYPE_INT (list)

    GET_LABEL_POINTER list_pointer (pLabel)
    WRITE_MEMORY pLabel 4 list FALSE 

    WHILE TRUE 
        WAIT 0 
        GET_LIST_SIZE list (j)

        IF bCallFunc = TRUE 
            CLEO_CALL LoadGtaVB 0 (/*Size*/sizeX /*RGBA*/r g b a /*time*/time, /*Texts pointers*/pTitle, pSubtitle, pText, pText2, iType, sprite, /*sprite offset*/offX, offY, /*sprite size*/fSizeX, fSizeY)
            bCallFunc = FALSE 
        ENDIF 
    ENDWHILE 
}
SCRIPT_END 

{
    LVAR_FLOAT sizeX // In
    LVAR_INT r g b a time pTitle pSubtitle pText pText2 iType sprite_icon// In     
    LVAR_FLOAT offX offY fSizeX fSizeY // In
    LVAR_INT iBoxes pLabel pScript bDelete list pAudio 
    LVAR_FLOAT fValue posX posY fVolume 

    LoadGtaVB:

    CONST_INT PLAY_AUDIO_STREAM 1

    // check ini file 
    IF READ_FLOAT_FROM_INI_FILE "cleo\Gtav_Msg_System.ini" "audio" "volume" (fVolume)
        IF fVolume > 1.0
            fVolume = 1.0
        ENDIF 
        IF fVolume < 0.0 
            fVolume = 0.0
        ENDIF 
    ENDIF

    // load audio stream 
    IF DOES_FILE_EXIST "cleo\aud\notification.mp3"
        LOAD_AUDIO_STREAM "cleo\aud\notification.mp3" (pAudio)
        SET_AUDIO_STREAM_VOLUME pAudio (fVolume)
        SET_AUDIO_STREAM_STATE pAudio PLAY_AUDIO_STREAM
    ENDIF 

    GET_LABEL_POINTER list_pointer (pLabel)
    READ_MEMORY pLabel 4 FALSE (list)
    GET_LIST_SIZE list (iBoxes)

    GET_LABEL_POINTER sprite_configs (pLabel)
    WRITE_STRUCT_PARAM pLabel 0 (offX)
    WRITE_STRUCT_PARAM pLabel 1 (offY)
    WRITE_STRUCT_PARAM pLabel 2 (fSizeX)
    WRITE_STRUCT_PARAM pLabel 3 (fSizeY) // the limit of variables... that sucks
    STREAM_CUSTOM_SCRIPT_FROM_LABEL Show_GtaV_Msg (sizeX r g b a time, iBoxes, pTitle, pSubtitle, pText, pText2, iType, sprite_icon)

    CLEO_RETURN 0 ()
} 

{ // External script
    LVAR_FLOAT sizeX // In
    LVAR_INT r g b a ms iBoxes pTitle pSubtitle pText pText2 iType sprite_icon// In
    LVAR_INT pLabel opacity sprite i id list j bActual bTest iFont 
    LVAR_FLOAT n k fH fPosX fTitleY fSubtY fTextY posX posY

    Show_GtaV_Msg:
    
    GET_LABEL_POINTER list_pointer (pLabel)
    READ_MEMORY pLabel 4 FALSE (list)

    posX = 70.0 
    posY = 280.0
    fTitleY = 245.0
    fSubtY = 255.0 
    fTextY = 270.0

    n = 150.0 
    n /= 2.0
    fH = posY 

    LOAD_TEXTURE_DICTIONARY GTAVMSG
    LOAD_SPRITE SPRITE_NOTF menugtav
    LOAD_SPRITE SPRITE_MSG_ICON dialog
     
    SWITCH (iBoxes)
        CASE 0
            id = FIRST_BOX_CREATED 
            LIST_ADD list (id)
        BREAK 
        CASE 1
            id = LAST_BOX_CREATED
            LIST_ADD list (id)
        BREAK  
        CASE 2 
            //IF NOT i = 0
                //--i
            //ENDIF 
            //LIST_REMOVE_INDEX list (i)
            id = NEW_BOX_CREATED 
            // climb the last box
            GET_LABEL_POINTER ActualBox (pLabel)
            WRITE_MEMORY pLabel 1 (1) FALSE 
            bActual = TRUE 
            CLEO_CALL CreatedBox 0 () 
            LIST_ADD list (id)
        BREAK 
    ENDSWITCH

    GET_LIST_SIZE list (i)
    
    n += 20.0
    k = posY - n

    TIMERB = 0 
    WHILE TIMERB < ms
        WAIT 0 
        GET_LIST_SIZE list (j) // actual list size
        iBoxes = j
    
        IF id = LAST_BOX_CREATED
        AND CLEO_CALL IsBoxUpRemoved 0 ()
            id = FIRST_BOX_CREATED
        ENDIF 

        GET_LABEL_POINTER ActualBox (pLabel)
        READ_MEMORY pLabel 1 FALSE (bActual)

        IF id = NEW_BOX_CREATED 
        AND CLEO_CALL IsChangeId 0 (2)
            id = LAST_BOX_CREATED 
        ENDIF 

        IF id = LAST_BOX_CREATED 
        AND j = 1 
            id = FIRST_BOX_CREATED 
        ENDIF 

        IF id = LAST_BOX_CREATED
        AND bActual = TRUE 
        AND NOT posY = k 
            posY -= 5.0
            fTitleY -= 5.0
            fSubtY -= 5.0 
            fTextY -= 5.0
            IF posY = k 
                GET_LABEL_POINTER ActualBox (pLabel)
                WRITE_MEMORY pLabel 1 (0) FALSE 
                id = FIRST_BOX_CREATED 
                CLEO_CALL FuncChangeId 0 (2)
            ENDIF 
        ENDIF 

        IF NOT j = i
            bActual = FALSE 
        ENDIF 
        
        GET_LABEL_POINTER Creating_box pLabel
        READ_MEMORY pLabel 1 FALSE (bTest)
        IF bTest = TRUE 
        AND id = FIRST_BOX_CREATED
            WRITE_MEMORY pLabel 1 (0) FALSE 
            BREAK 
        ENDIF 

        IF NOT posY = k 
        AND j = 2 
        AND id = FIRST_BOX_CREATED
            posY -= 5.0
            fTitleY -= 5.0
            fSubtY -= 5.0 
            fTextY -= 5.0
        ENDIF
        //PRINT_FORMATTED_NOW "bBefore: %i" 2000 (bBefore)

        IF opacity < a 
            opacity += 20
        ENDIF 

        GET_TEXTURE_FROM_SPRITE SPRITE_NOTF (sprite)
        
        GOSUB ShowInterface
    ENDWHILE 
    
    GET_LIST_SIZE list (iBoxes)
    IF NOT iBoxes = 0
        --iBoxes 
    ENDIF 
    LIST_REMOVE_INDEX list (iBoxes) 

    FREE_MEMORY pTitle 
    FREE_MEMORY pSubtitle 
    FREE_MEMORY pText
    FREE_MEMORY pText2 

    TERMINATE_THIS_CUSTOM_SCRIPT

    ShowInterface: 

        posX = sizeX * 0.25 
        DRAW_TEXTURE_PLUS sprite DRAW_EVENT_AFTER_FADE (posX, posY) (sizeX, 150.0) 180.0 0.0 TRUE 0 0 (r, g, b, opacity)
        
        SWITCH (iType)
            CASE NORMAL_MESSAGE_BOX
                iFont = FONT_PRICEDOWN
            BREAK
            CASE PEOPLE_MESSAGE_BOX 
                iFont = FONT_SUBTITLES
            BREAK 
        ENDSWITCH 

        IF NOT sprite_icon = 0
            CLEO_CALL ShowIcons 0 (sprite_icon)
        ENDIF 

        posX = sizeX * 0.16071 
        IF NOT pTitle = 0 
            DRAW_STRING $pTitle DRAW_EVENT_AFTER_FADE posX fTitleY (0.4 0.6) ON iFont // limit: 15 characters
        ENDIF 

        IF NOT pSubtitle = 0
        AND iType = NORMAL_MESSAGE_BOX
            DRAW_STRING $pSubtitle DRAW_EVENT_AFTER_FADE posX fSubtY (0.2 0.4) TRUE FONT_SUBTITLES // limit: 31 characters
        ENDIF 

        fH = fTextY + 25.0
        posX = sizeX * 0.035714 
        sizeX /= 2.0 // wrap
        DRAW_STRING_EXT $pText DRAW_EVENT_AFTER_FADE posX fTextY (0.3 0.5) ON FONT_SUBTITLES TRUE ALIGN_LEFT sizeX FALSE 255 255 255 255 1 0 /*DropShadowColor*/ 0 0 0 255 FALSE 0 0 0 255 
        
        IF NOT pText2 = 0
            DRAW_STRING_EXT $pText2 DRAW_EVENT_AFTER_FADE posX fH (0.3 0.5) ON FONT_SUBTITLES TRUE ALIGN_LEFT sizeX FALSE 255 255 255 255 1 0 /*DropShadowColor*/ 0 0 0 255 FALSE 0 0 0 255 // limit: 128 characters 
        ENDIF 
        sizeX *= 2.0 // to save vars 
    RETURN
}

sprite_configs:
DUMP 
00 00 00 00 // offset X
00 00 00 00 // offset Y
00 00 00 00 // size X
00 00 00 00 // size Y
ENDDUMP 

{
    LVAR_INT sprite // In 
    LVAR_FLOAT offX offY sizeX sizeY
    LVAR_INT pLabel
    LVAR_FLOAT x y 

    ShowIcons:
        x = 17.0 
        y = 247.0 
        
        GET_LABEL_POINTER sprite_configs (pLabel)
        READ_STRUCT_PARAM pLabel 0 (offX)
        READ_STRUCT_PARAM pLabel 1 (offY)
        READ_STRUCT_PARAM pLabel 2 (sizeX)
        READ_STRUCT_PARAM pLabel 3 (sizeY)
        x += offX 
        y += offY
        DRAW_TEXTURE_PLUS sprite DRAW_EVENT_AFTER_FADE x y sizeX sizeY 0.0 0.0 TRUE 0 0 255 255 255 220
    CLEO_RETURN 0 ()
}

{
    LVAR_INT id // In
    LVAR_INT pLabel 

    FuncChangeId:
        GET_LABEL_POINTER ChangeId (pLabel)
        WRITE_MEMORY pLabel 1 (id) FALSE 
    CLEO_RETURN 0 ()
}

{
    LVAR_INT id // In
    LVAR_INT pLabel j

    IsChangeId:
        GET_LABEL_POINTER ChangeId (pLabel)
        READ_MEMORY pLabel 1 FALSE (j)
        IF j = id
            WRITE_MEMORY pLabel 1 (0) FALSE 
            RETURN_TRUE 
        ELSE 
            RETURN_FALSE 
        ENDIF 
    CLEO_RETURN 0 ()
}

{
    LVAR_INT pLabel 

    CreatedBox:
        GET_LABEL_POINTER Creating_box (pLabel) 
        WRITE_MEMORY pLabel 1 (1) FALSE 
    CLEO_RETURN 0 ()
}

{
    LVAR_INT pLabel bDeleted

    IsBoxUpRemoved: 
        GET_LABEL_POINTER RemoveBeforeBox (pLabel)
        READ_MEMORY pLabel 1 FALSE (bDeleted)   

        IF bDeleted = TRUE 
            WRITE_MEMORY pLabel 1 (0) FALSE 
            RETURN_TRUE 
        ELSE 
            RETURN_FALSE 
        ENDIF 
    CLEO_RETURN 0 ()
}

{
    LVAR_INT pLabel, list, i 

    RemoveUpBox:
        GET_LABEL_POINTER list_pointer (pLabel)
        READ_MEMORY pLabel 4 FALSE (list)
        GET_LIST_SIZE list i
        LIST_REMOVE_INDEX list i

        GET_LABEL_POINTER RemoveBeforeBox (pLabel)
        WRITE_MEMORY pLabel 1 (1) FALSE 
    CLEO_RETURN 0 ()
}

{
    LVAR_INT bActual pLabel 

    IsActualBox:

}
ChangeId:
DUMP 
00 
ENDDUMP 

ActualBox:
DUMP 
00 
ENDDUMP 

MoveBBox: // bool 
DUMP 
00 
ENDDUMP 

Boxes:
DUMP 
00 00 
ENDDUMP 

RemoveBeforeBox:
DUMP 
00 
ENDDUMP 

Creating_box: // bool 
DUMP 
00 
ENDDUMP 

list_pointer:
DUMP 
00 00 00 00 
ENDDUMP 

