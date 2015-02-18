AppTitle"SpriteMasterPro - Basic example"

Include "..\includes\smPro.bb"



SpriteGraphics 280,240,0,2                   ; set up sprite display

AmbientLight 2,2,2

pack1=LoadPack("..\packs\testpack.png")      ; load pack
sprite.smSprite=GetPackedSprite("sun",pack1) ; extract sprite
PositionSprite sprite,70,60                  ; position sprite

RenderWorld : Flip : WaitKey

FreePack pack1     ; free up pack and associated sprites
EndSpriteGraphics  ; close down sprite display
End