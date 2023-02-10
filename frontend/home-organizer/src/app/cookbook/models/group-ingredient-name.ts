import {RecipeComp} from "./recipe-comp";

export class GroupIngredientName {
  id: string;
  name: string;
  childIngredientNames: RecipeComp[] = [];
  hidden: boolean = false;


  constructor(id: string, name: string, ...children: RecipeComp[]) {
    this.id = id;
    this.name = name;
    children.forEach(value => this.childIngredientNames.push(value))
  }


}
