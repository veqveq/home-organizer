import {RecipeComp} from "./recipe-comp";
import {Ingredient} from "./ingredient";

export interface Recipe{
  id: string,
  title: string,
  kcal?: bigint,
  proteins?: bigint,
  fats?: bigint,
  carbons?: bigint,
  portions?: bigint,
  cookTime: bigint,
  description?: string,
  likes?: bigint,
  dislikes?: bigint,
  rating?: bigint,
  imageUrl: string,
  url: string,
  type: RecipeComp,
  category: RecipeComp,
  kitchen: RecipeComp,
  ingredient: Ingredient[]
}
