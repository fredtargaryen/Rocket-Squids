# Creating Statue Models

## Opening the Statue

Making the statue look open in Blockbench is easy:
- Select the `door` box in the model's hierarchy
- Set its Y rotation to -90
- Select the `door`'s faces in the UV editor and move them down to the textures underneath, until it looks like a conch has been inserted into the shape in the door

Likewise to "close" the model set the `door` rotation to 0 and move the faces back up. Please only commit the model in the closed position.

## Exporting Statue Models
There are currently 5 models to export from `statue_closed.bbmodel` in Blockbench for statues:
- The item models are shrunken versions of the entire statue.
  - `models/block/statue_item_closed`: the item model for closed statues in the inventory
    - Export the model as-is (File > Export > Export Block/Item Model)
  - `models/block/statue_item_open`: the item model for open statues in the inventory
    - Change the model to open (see [Opening the Statue](README.md#opening-the-statue))
    - Export the model
- The block model is divided into upper and lower blocks to make a multiblock statue. This is good practice but also avoids issues with Minecraft's lighting system.
  - `models/block/statue_lower_closed`: the lower block when the door is closed
    - Delete the `upper` group of boxes
    - Export the model
  - `models/block/statue_lower_open`: the lower block when the door is open
    - Change the model to open (see [Opening the Statue](README.md#opening-the-statue))
    - Delete the `upper` group of boxes
    - Export the model
  - `models/block/statue_upper`: the upper block looks the same whether the statue is open or closed
    - Delete the `lower` group of boxes
    - Move the `upper` group of boxes down 16 units, so that the model is sitting on the "floor" of the model space
    - Export the model

## After Exporting
Textures are set up for the model editor's convenience in Blockbench but need to be manually set after editing to look correct in-game. In the .json for every exported model replace the `textures` group with this:
```
	"textures": {
		"model": "rocketsquids:block/statue",
		"particle": "block/stone"
	},
```

## Creating the Statue Texture

Some significant noise is added to the statue texture to give the impression of general weathering over time as well as fit into Minecraft's art style a bit better.

An easy way to do this is in Paint.NET:
- Select Effects > Noise > Add Noise...
- Set Intensity to 15
- Set Color Saturation to 0
- Set Coverage to 100
- Randomize to taste and press OK
- Save the new texture to `textures/block/statue.png`.