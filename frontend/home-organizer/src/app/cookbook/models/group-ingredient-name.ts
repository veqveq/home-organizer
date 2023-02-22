import {RecipeComp} from "./recipe-comp";
import {OnNamed} from "../../core/models/onNamed";
import {OnEqual} from "../../core/models/onEqual";

export class GroupIngredientName extends OnEqual implements OnNamed {
  id: string;
  name: string;
  childIngredientNames: RecipeComp[] = [];
  hidden: boolean = false;


  constructor(id: string, name: string, ...children: RecipeComp[]) {
    super()
    this.id = id;
    this.name = name;
    children.forEach(value => this.childIngredientNames.push(value))
  }

  protected onEqual(obj: GroupIngredientName): boolean {
    return this.id === obj.id && this.name === obj.name;
  }
}
