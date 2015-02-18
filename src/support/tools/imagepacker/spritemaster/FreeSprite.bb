AppTitle "SpriteMaster Pro - FreeSprite"

Include "..\includes\smPro.bb"

Const sw=640 , sh=480
SpriteGraphics sw,sh
CameraClsMode spritecamera,False,True
ClsColor 60,70,90 : Cls
SetFont LoadFont("courier",20,1)

; load the sprite pack
Global pack1=LoadPack("..\packs\testpack.png")

; get sprites from pack
s1.smSprite=GetPackedSprite(4,pack1)
s2.smSprite=GetPackedSprite(6,pack1)
s3.smSprite=GetPackedSprite(8,pack1)
s4.smSprite=GetPackedSprite(3,pack1)

; position all sprites on screen
PositionSprite s1,54,42
PositionSprite s2,155,120
PositionSprite s3,250,170
PositionSprite s4,350,70

; remove sprites
Render "Press key to remove "+s2\name$+" ..."
FreeSprite s2
Render "Press key to remove "+s4\name$+" ..."
FreeSprite s4
Render "Press key to remove "+s1\name$+" ..."
FreeSprite s1
Render "Press key to remove "+s3\name$+" ..."
FreeSprite s3
Render "Done. Press key to end ..."

; cleanup
FreePack pack1
EndSpriteGraphics
End

Function Render(msg$)
	Cls
	RenderWorld
	Text sw/2,8,"FreeSprite sprite",True
	Text 30,sh*0.75,msg$
	Text 30,sh-24,"Sprites using pack1 = "+CountSpritesUsingPack(pack1)
	Flip : WaitKey
End Function