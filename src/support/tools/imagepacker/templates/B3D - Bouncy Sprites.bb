; Pack Viewer - Generated from Image Packer

AppTitle "SpriteMaster Pro - Bounce Sprites example"
%COPYINCLUDEFILE%smPro.bb
Include "smPro.bb"

Const sw=640 , sh=480
SpriteGraphics sw,sh
CameraClsMode spritecamera,True,False
CameraClsColor spritecamera,60,70,90
SetFont LoadFont("tahoma",19)

; load the pack
%GLOB%%PACKNAME%=LoadPack("%PACKFILENAME%")
numSprites=CountPackedImages(%PACKNAME%)

; grab all sprites from the pack
For n=1 To numSprites
	sprite.smSprite=GetPackedSprite(n,%PACKNAME%)
	sprite\x=Rand(20,sw-sprite\width-20)
	sprite\y=Rand(20,sh-sprite\height-20)
	; use var[0] and var[1] for dx/dy bounce
	sprite\var[0]=Rand(1,3) : If Rnd(1)<0.5 sprite\var[0]=-sprite\var[0]
	sprite\var[1]=Rand(1,3) : If Rnd(1)<0.5 sprite\var[1]=-sprite\var[1]
Next


Repeat

	count=count+1

	For sprite.smSprite=Each smSprite

		; reflect if sprite hits screen edge
		If sprite\x<1 Or sprite\x>(sw-sprite\width) sprite\var[0]=-sprite\var[0] ; dx=-dx
		If sprite\y<1 Or sprite\y>(sh-sprite\height) sprite\var[1]=-sprite\var[1] ; dy=-dy
		sprite\x=sprite\x+sprite\var[0]
		sprite\y=sprite\y+sprite\var[1]

		If count Mod 6=0
			PositionSprite sprite,sprite\x,sprite\y,sprite\frame+1
		Else
			PositionSprite sprite,sprite\x,sprite\y
		EndIf
	Next

	RenderWorld

	Text sw/2,8,"SpriteMaster Pro",True
	Text 15,sh-30,"Sprites in pack: "+Str$(numSprites)
	Text sw/2,sh-50,"Hold SPACE to turn off frame sync",1
	
	; FPS timer
	If MilliSecs()>oldTime
	   oldTime=MilliSecs()+1000
	   fps=frameCount : frameCount=0
	Else
	   frameCount=frameCount+1
	EndIf
	Text 10,10,fps+" FPS"

	Flip Not KeyDown(57)

Until KeyHit(1)

; cleanup
FreePack %PACKNAME%
EndSpriteGraphics
End