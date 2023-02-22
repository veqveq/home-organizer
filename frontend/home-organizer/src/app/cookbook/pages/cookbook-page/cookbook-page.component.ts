import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {Recipe} from "../../models/recipe";
import {RecipeFilter} from "../../models/recipe-filter";
import {RecipeService} from "../../services/recipe.service";
import {SliderFilterComponent} from "../../components/slider-filter/slider-filter.component";
import {ItemFilterComponent} from "../../components/item-filter/item-filter.component";
import {DropdownTabComponent} from "../../../core/components/dropdown-tab/dropdown-tab.component";
import {map, tap} from "rxjs";
import {LoaderPosition} from "../../../core/components/loader/loader-position-enum";
import {IngredientFilterComponent} from "../../components/ingredient-filter/ingredient-filter.component";

@Component({
  selector: 'app-cookbook-page',
  templateUrl: './cookbook-page.component.html'
})
export class CookbookPageComponent implements OnInit {
  @ViewChild('ratingOptions') ratingOptions: SliderFilterComponent
  @ViewChild('typeOptions') typeOptions: ItemFilterComponent
  @ViewChild('categoryOptions') categoryOptions: ItemFilterComponent
  @ViewChild('kitchenOptions') kitchenOptions: ItemFilterComponent
  @ViewChild('ingredientOptions') ingredientOptions: IngredientFilterComponent
  @ViewChild('additionalOptions') additionalOptions: DropdownTabComponent
  @ViewChild('energyOptions') energyOptions: DropdownTabComponent
  @ViewChild('sortParam') sortParam: ElementRef
  @ViewChild('page') recipeList: ElementRef

  filter: RecipeFilter = new RecipeFilter();
  page: number = 0
  totalPages: number
  totalElements: number
  direction: string = 'DESC'
  loader: boolean = false
  public loaderPosition = LoaderPosition

  recipes: Recipe[] = []

  constructor(
    private service: RecipeService
  ) {
  }

  ngOnInit(): void {
    this.doFilter()
  }

  reset() {
    this.filter = new RecipeFilter()
    this.ratingOptions.reset()
    this.typeOptions.reset()
    this.categoryOptions.reset()
    this.kitchenOptions.reset()
    this.ingredientOptions.reset()
    this.additionalOptions.doReset()
    this.energyOptions.doReset()
    this.recipeList.nativeElement.scrollTop = 0
    this.doFilter()
  }

  doFilter() {
    this.loader = true
    this.service.filter(this.filter, this.getSort(), 0).pipe(
      tap(page => {
        this.page = page.number + 1
        this.totalPages = page.totalPages
        this.totalElements = page.totalElements
        this.recipeList.nativeElement.scrollTop = 0
      }),
      map((page) => page.content)
    ).subscribe((content) => {
      this.recipes = content
      this.loader = false
    })
  }

  changeDirection() {
    if (this.direction == 'ASC') {
      this.direction = 'DESC'
    } else {
      this.direction = 'ASC'
    }
  }

  getSort(): string {
    if (this.sortParam) {
      return this.sortParam.nativeElement.value + ',' + this.direction
    }
    return ''
  }

  doScroll(menuList: HTMLUListElement) {
    if (menuList.scrollHeight - menuList.scrollTop === menuList.clientHeight) {
      this.doFilterNextPage()
    }
  }

  doFilterNextPage() {
    if (this.page < this.totalPages - 1) {
      this.loader = true
      this.service.filter(this.filter, this.getSort(), this.page)
        .pipe(
          tap(resp => {
            if (!resp.last) {
              this.page = resp.number + 1
            }
          }),
          map((resp) => resp.content)
        )
        .subscribe(items => {
          this.recipes.push(...items)
          this.loader = false
        })
    }
  }
}
