AppTitle "SpriteMaster Pro - Text (Show characters)"

; the include file
Include "..\includes\smPro.bb"

Const sw=400,sh=300

SpriteGraphics sw,sh
CameraClsMode spritecamera,False,True
ClsColor 180,110,110 : Cls
SetFont LoadFont("Tahoma",18,1)

; load pack containing font
txt.smSprite=CreateTextSprite("..\packs\fontpack.png",1)

; set up a string with font characters
For a=32 To 127
	t$=t$+Chr$(a)
	If (a Mod 16)=0 t$=t$+Chr$(10)
Next

AddText txt,20,20,t$
	
RenderWorld
Flip
WaitKey

; cleanup
FreeSprite txt
EndSpriteGraphics

End