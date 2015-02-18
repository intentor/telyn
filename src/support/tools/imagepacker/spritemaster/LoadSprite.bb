AppTitle "SpriteMaster Pro - LoadSprite()"

Include "..\includes\smPro.bb"

Const sw=560,sh=300

; set up 3d display -> gfxmode/camera/sprite pivot
SpriteGraphics sw,sh,0,2
CameraClsMode spritecamera,False,True
ClsColor 200,210,210 : Cls
SetFont LoadFont("courier",14,1)

spw=194 : sph=52

; create a sprite and position it on screen
s.smSprite=LoadSprite("testimage.png")
PositionSprite s,80,55

RenderWorld

Color 1,1,1
Text 10,10,"sprite.smSprite=LoadSprite(filename$[,flags][,parent])"
Text sw/2,sh*0.4,"Sprites name = "+s\name$
Color 200,1,1
Text 10,sh-90,"Note: To avoid blurry sprite images keep the width/height"
Text 10,sh-70,"      of the loaded image to a 'power of 2' value:"
Color 100,100,100
Text 10,sh-40,"       2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, ..."

Flip : WaitKey

; cleanup
FreeSprite s
EndSpriteGraphics
End