import {ValInterval} from "./val-interval";
import {RecipeComp} from "./recipe-comp";
import {TSMap} from "typescript-map";
import {GroupIngredientName} from "./group-ingredient-name";

export class RecipeFilter{
  title?: string;
  kcal: ValInterval<number> = new ValInterval<number>();
  proteins: ValInterval<number> = new ValInterval<number>();
  fats: ValInterval<number> = new ValInterval<number>();
  carbons: ValInterval<number> = new ValInterval<number>();
  portions: ValInterval<number> = new ValInterval<number>();
  cookTime: ValInterval<number> = new ValInterval<number>();
  rating: ValInterval<number> = new ValInterval<number>();
  typeIds: string[] = [];
  categoryIds: string[] = [];
  kitchenIds: string[] = [];
  ingredients: Map<GroupIngredientName,RecipeComp[]> = new Map<GroupIngredientName, RecipeComp[]>();

  toJson(): any{
    let ingredientIdsMap = new TSMap<string,string[]>()
    this.ingredients.forEach((ingredients, group) =>
      ingredientIdsMap.set(group.id,ingredients.map(ingredient => ingredient.id)))
    return {
      "title": this.title,
      "kcal": this.kcal,
      "proteins": this.proteins,
      "fats": this.fats,
      "carbons": this.carbons,
      "portions": this.portions,
      "cookTime": this.cookTime,
      "rating": this.rating,
      "typeIds": this.typeIds,
      "categoryIds": this.categoryIds,
      "kitchenIds": this.kitchenIds,
      "ingredients": ingredientIdsMap
    }
  }
}
