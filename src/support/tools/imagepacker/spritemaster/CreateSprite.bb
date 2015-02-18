AppTitle "SpriteMaster Pro - CreateSprite()"

; the include file
Include "..\includes\smPro.bb"

Const sw=800,sh=400

; set up 3d display -> gfxmode/camera/sprite pivot
SpriteGraphics sw,sh,0,2
CameraClsMode spritecamera,False,True
ClsColor 200,170,210 : Cls
SetFont LoadFont("courier",14,1)

spw=194 : sph=52

; create a sprite and position it on screen
s.smSprite=CreateSprite(spw,sph)
PositionSprite s,sw/2,80

; get the pack used by the sprite created above
pack=GetPack(s)

; create a texture for the sprite
tex=CreateTexture(spw,sph)
SetBuffer TextureBuffer(tex)
Color 255,255,255
Rect 0,0,TextureWidth(tex),TextureHeight(tex),True
Color 50,50,50
Text 2,2,"Sprite created with"
Text 2,32,"CreateSprite()"
SetBuffer BackBuffer()

; apply texture to sprite
SpriteTexture s,tex

; make a few copies
For n=1 To 10
	s2.smSprite=CopySprite(s)
	SetSprite s2,1,50+n*8,50+n*12 , 0,0 , -8+n*3 , 40+n*18,80+n*3,100+10
Next

RenderWorld

Text 10,10,"sprite.smSprite=CreateSprite(width,height,framesH,framesV[,flags][,parent])"
Text 10,sh-50,"Note: Copied sprites share the pack created with CreateSprite()"
Text 10,sh-30,"Total number of sprites using the pack = "+CountSpritesUsingPack(pack)

Flip : WaitKey

; cleanup
FreePack pack
EndSpriteGraphics
End