import {ValInterval} from "./val-interval";
import {RecipeComp} from "./recipe-comp";
import {TSMap} from "typescript-map";

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
  ingredients: TSMap<string,RecipeComp[]> = new TSMap<string, RecipeComp[]>();
}
