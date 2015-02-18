; Pack Viewer - Generated from Image Packer

%COPYINCLUDEFILE%smPro.bb
Include "smPro.bb"

Const sw=640 , sh=480
SpriteGraphics sw,sh
CameraClsMode spritecamera,True,False
CameraClsColor spritecamera,60,70,90 : Cls

; load the sprite pack
%GLOB%%PACKNAME%=LoadPack("%PACKFILENAME%")
%GLOB%numSprites=CountPackedImages(%PACKNAME%)

; get all sprites from pack
%ALL%
%GLOB%%IMAGENAME%.smSprite=GetPackedSprite("%IMAGENAME%",%PACKNAME%)

; position all sprites on screen
%ALL%
PositionSprite %IMAGENAME%,Rand(5,sw-50),Rand(5,sh-50)

RenderWorld

Text sw/2,8,"SpriteMaster Pro",True
Text 15,sh-30,"Sprites in pack: "+Str$(numSprites)

Flip : WaitKey

; cleanup
FreePack %PACKNAME%
EndSpriteGraphics
End