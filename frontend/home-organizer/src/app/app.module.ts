import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {DictionaryComponent} from "./dictionary/components/dictionary/dictionary.component";
import {HttpClientModule} from "@angular/common/http";
import {GlobalErrorComponent} from './core/components/global-error/global-error.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {DictionaryFilterPipe} from './dictionary/pipes/dictionary-filter.pipe';
import {ModalComponent} from "./core/components/modal/modal.component";
import {DictionaryEditorComponent} from './dictionary/components/dictionary-editor/dictionary-editor.component';
import {CreateDictionaryFieldComponent} from './dictionary/components/dictionary-field-editor/dictionary-field-editor.component';
import {DictionaryRegisterComponent} from './dictionary/pages/dictionary-register/dictionary-register.component';
import {RefDirective} from "./core/directives/ref.directive";
import {DictionaryPageComponent} from './dictionary/pages/dictionary-page/dictionary-page.component';
import { DictionaryItemEditorComponent } from './dictionary/components/dictionary-item-editor/dictionary-item-editor.component';
import { PaginationComponent } from './core/components/pagination/pagination.component';
import { LoaderComponent } from './core/components/loader/loader.component';
import { CookbookPageComponent } from './cookbook/pages/cookbook-page/cookbook-page.component';
import { RecipeComponent } from './cookbook/components/recipe/recipe.component';
import { Ng5SliderModule } from 'ng5-slider';
import { SliderFilterComponent } from './cookbook/components/slider-filter/slider-filter.component';
import { ItemFilterComponent } from './cookbook/components/item-filter/item-filter.component';
import { DropdownTabComponent } from './core/components/dropdown-tab/dropdown-tab.component';

@NgModule({
  declarations: [
    AppComponent,
    DictionaryComponent,
    GlobalErrorComponent,
    DictionaryFilterPipe,
    ModalComponent,
    DictionaryEditorComponent,
    RefDirective,
    CreateDictionaryFieldComponent,
    DictionaryRegisterComponent,
    DictionaryPageComponent,
    DictionaryItemEditorComponent,
    PaginationComponent,
    LoaderComponent,
    CookbookPageComponent,
    RecipeComponent,
    SliderFilterComponent,
    ItemFilterComponent,
    DropdownTabComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    Ng5SliderModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
