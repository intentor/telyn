AppTitle "SpriteMaster Pro - GetPackedSprite()"

; the include file
Include "..\includes\smPro.bb"

Const sw=640,sh=480

; set up 3d display -> gfxmode/camera/sprite pivot
SpriteGraphics sw,sh
CameraClsMode spritecamera,False,True
ClsColor 50,60,80 : Cls
SetFont LoadFont("tahoma",14,1)

s1=9
s2$="floppy"

; load image pack and create single-surface sprites
pack1=LoadPack("..\packs\testpack.png")
; get sprites from pack
sprite1.smSprite=GetPackedSprite(s1,pack1)
sprite2.smSprite=getpackedsprite(s2$,pack1)
PositionSprite sprite1,080,260
PositionSprite sprite2,360,260

RenderWorld
Color 180,180,180
Text sw/2,5,"sprite.smSprite=GetPackedSprite ( index/name$ , pack )",1
Color 255,255,255
Text 15,50,"The GetPackedSprite() function gets a sprite from a previously loaded pack."
Text 15,70,"There are two methods which can be used to fetch sprites:"
Color 50,200,50
Text 15,100,"pack1=LoadSpritePack("+Chr$(34)+"pack 1.png"+Chr$(34)+")"
Text 15,120,"sprite1.smSprite=GetPackedSprite("+Str$(s1)+",pack1)               ; get sprite via it's index number"
Text 15,140,"sprite2.smSprite=GetPackedSprite("+Chr$(34)+s2$+Chr$(34)+",pack1)    ; get sprite via it's name"
Color 255,255,255
Text sw/2,180,"Here are the two examples from above",1
Color 50,200,50
Text 85,210,"sprite index number="+Str$(s1)
Text 360,210,"sprite name="+s2$
Flip : WaitKey

; cleanup
FreePack pack1
EndSpriteGraphics

End