AppTitle "SpriteMaster Pro - Text (Basic Example)"

Include "..\includes\smPro.bb"

Const sw=400,sh=200

; set up 3d display -> gfxmode/camera/sprite pivot
SpriteGraphics sw,sh,0,2

; text sprite created (using an offset of 1 character)
txt.smSprite=CreateTextSprite("..\packs\fontpack.png",1)

; characters added to 'text' sprite
AddText txt,50,50,"Hello World!",255,255,255,1.0
AddText txt,12,80,"All characters",155,15,255,1.0
AddText txt,8,110,"here are using a ",200,100,20
AddText txt,8,140,"Single-Surface",250,250,30,0.7

RenderWorld
Text 10,10,("Press key for next messsage ..")
Flip : WaitKey

; clear characters from text sprite
ClearText txt

AddText txt,10,040,"Each character"
AddText txt,10,070,"can have its own" , 180,180,180
AddText txt,10,100,"color / alpha ..." , 90,90,90
RenderWorld

Text 10,10,("Press key for next messsage ..")
Flip : WaitKey

; clear characters from text sprite
ClearText txt
AddText txt,40,80,"C",200,100,20
AddText txt,60,80,"o",100,200,200
AddText txt,80,80,"o",10,100,200
AddText txt,100,80,"l",200,250,20
AddText txt,120,80,"!",10,120,20

RenderWorld
Text 10,10,("Done. Press key to end ..")
Flip : WaitKey

; cleanup
FreeSprite txt
EndSpriteGraphics
End