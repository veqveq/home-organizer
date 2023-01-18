import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {Recipe} from "../../models/recipe";
import {RecipeFilter} from "../../models/recipe-filter";
import {ValInterval} from "../../models/val-interval";
import {RecipeService} from "../../services/recipe.service";
import {SliderFilterComponent} from "../../components/slider-filter/slider-filter.component";
import {ItemFilterComponent} from "../../components/item-filter/item-filter.component";
import {DropdownTabComponent} from "../../../core/components/dropdown-tab/dropdown-tab.component";
import {map, tap} from "rxjs";

@Component({
  selector: 'app-cookbook-page',
  templateUrl: './cookbook-page.component.html'
})
export class CookbookPageComponent implements OnInit {
  @ViewChild('ratingOptions') ratingOptions: SliderFilterComponent
  @ViewChild('typeOptions') typeOptions: ItemFilterComponent
  @ViewChild('categoryOptions') categoryOptions: ItemFilterComponent
  @ViewChild('kitchenOptions') kitchenOptions: ItemFilterComponent
  @ViewChild('ingredientOptions') ingredientOptions: ItemFilterComponent
  @ViewChild('additionalOptions') additionalOptions: DropdownTabComponent
  @ViewChild('energyOptions') energyOptions: DropdownTabComponent
  @ViewChild('sortParam') sortParam: ElementRef
  @ViewChild('page') recipeList: ElementRef

  filter: RecipeFilter = this.initNewFilter()
  page: number = 0
  totalPages: number
  totalElements: number
  direction: string = 'DESC'

  recipes: Recipe[] = []

  constructor(
    private service: RecipeService
  ) {
  }

  ngOnInit(): void {
    this.doFilter()
  }

  initNewFilter(): RecipeFilter {
    return new class implements RecipeFilter {
      carbons: ValInterval<number> = new class implements ValInterval<number> {
        from: number;
        to: number;
      };
      categoryIds: string[];
      cookTime: ValInterval<number> = new class implements ValInterval<number> {
        from: number;
        to: number;
      };
      fats: ValInterval<number> = new class implements ValInterval<number> {
        from: number;
        to: number;
      };
      ingredientIds: string[];
      kcal: ValInterval<number> = new class implements ValInterval<number> {
        from: number;
        to: number;
      };
      kitchenIds: string[];
      portions: ValInterval<number> = new class implements ValInterval<number> {
        from: number;
        to: number;
      };
      proteins: ValInterval<number> = new class implements ValInterval<number> {
        from: number;
        to: number;
      };
      rating: ValInterval<number> = new class implements ValInterval<number> {
        from: number;
        to: number;
      };
      title: string;
      typeIds: string[];
    }
  }

  reset() {
    this.filter = this.initNewFilter()
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
    this.service.filter(this.filter, this.getSort(), 0).pipe(
      tap(page => {
        this.page = page.number + 1
        this.totalPages = page.totalPages
        this.totalElements = page.totalElements
        this.recipeList.nativeElement.scrollTop = 0
      }),
      map((page) => page.content)
    ).subscribe((content) => this.recipes = content)
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
      this.service.filter(this.filter, this.getSort(), this.page)
        .pipe(
          tap(resp => {
            if (!resp.last) {
              this.page = resp.number + 1
            }
          }),
          map((resp) => resp.content)
        )
        .subscribe(items => this.recipes.push(...items))
    }
  }
}