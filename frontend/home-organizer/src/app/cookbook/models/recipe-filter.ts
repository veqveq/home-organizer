import {ValInterval} from "./val-interval";

export interface RecipeFilter{
  title?: string,
  kcal: ValInterval<number>,
  proteins: ValInterval<number>,
  fats: ValInterval<number>,
  carbons: ValInterval<number>,
  portions: ValInterval<number>,
  cookTime: ValInterval<number>,
  rating: ValInterval<number>,
  typeIds: string[],
  categoryIds: string[],
  kitchenIds: string[],
  ingredientIds: string[],
}
