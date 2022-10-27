import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {DictionaryComponent} from "./components/dictionary/dictionary.component";
import {HttpClientModule} from "@angular/common/http";
import {GlobalErrorComponent} from './components/global-error/global-error.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {DictionaryFilterPipe} from './pipes/dictionary-filter.pipe';
import {ModalComponent} from "./components/modal/modal.component";
import {DictionaryEditorComponent} from './components/dictionary-editor/dictionary-editor.component';
import {FocusDirective} from './directives/focus.directive';
import {CreateDictionaryFieldComponent} from './components/dictionary-field-editor/dictionary-field-editor.component';
import {DictionaryRegisterComponent} from './pages/dictionary-register/dictionary-register.component';
import {RefDirective} from "./directives/ref.directive";
import {DictionaryPageComponent} from './pages/dictionary-page/dictionary-page.component';
import { DictionaryItemEditorComponent } from './components/dictionary-item-editor/dictionary-item-editor.component';
import { PaginationComponent } from './components/pagination/pagination.component';

@NgModule({
  declarations: [
    AppComponent,
    DictionaryComponent,
    GlobalErrorComponent,
    DictionaryFilterPipe,
    ModalComponent,
    DictionaryEditorComponent,
    FocusDirective,
    RefDirective,
    CreateDictionaryFieldComponent,
    DictionaryRegisterComponent,
    DictionaryPageComponent,
    DictionaryItemEditorComponent,
    PaginationComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
