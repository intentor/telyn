AppTitle "SpriteMaster Pro - 2D/3D comparison"

Include "..\includes\smPro.bb"

SpriteGraphics 320,200,0,2                     ; set up sprite display
CameraClsColor spritecamera,120,90,80

pack1=LoadPack("..\packs\testpack.png")        ; load pack
sprite.smSPrite=GetPackedSprite("dice",pack1)  ; extract sprite
image=GetPackedImage("dice",pack1)             ; extract 2d image

PositionSprite sprite,180,60                   ; position sprite
RenderWorld
DrawImage image,40,60                          ; position image

Text 40,40,"2D Image"
Text 180,40,"3D Sprite"

Flip
WaitKey

FreeImage image
FreePack pack1           ; free up pack and associated sprites
EndSpriteGraphics        ; close down sprite display
End