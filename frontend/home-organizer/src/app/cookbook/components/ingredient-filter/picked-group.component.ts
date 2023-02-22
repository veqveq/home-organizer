import {Component} from '@angular/core';
import {CloseableGroupComponent} from "../../../core/components/closeable-group/closeable-group.component";
import {RecipeComp} from "../../models/recipe-comp";
import {GroupIngredientName} from "../../models/group-ingredient-name";

@Component({
  selector: 'app-picked-group',
  templateUrl: '../../../core/components/closeable-group/closeable-group.component.html',
})
export class PickedGroupComponent extends CloseableGroupComponent<GroupIngredientName,RecipeComp>{

  constructor() {
    super();
  }
  ngOnInit(): void {
  }

}
