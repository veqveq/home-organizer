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
import {CreateDictionaryComponent} from './components/create-dictionary/create-dictionary.component';
import {FocusDirective} from './directives/focus.directive';
import {CreateDictionaryFieldComponent} from './components/create-dictionary-field/create-dictionary-field.component';
import {DictionaryRegisterComponent} from './pages/dictionary-register/dictionary-register.component';
import {HomeComponent} from './pages/home/home.component';
import {RefDirective} from "./directives/ref.directive";
import {DictionaryPageComponent} from './pages/dictionary-page/dictionary-page.component';

@NgModule({
  declarations: [
    AppComponent,
    DictionaryComponent,
    GlobalErrorComponent,
    DictionaryFilterPipe,
    ModalComponent,
    CreateDictionaryComponent,
    FocusDirective,
    RefDirective,
    CreateDictionaryFieldComponent,
    DictionaryRegisterComponent,
    HomeComponent,
    DictionaryPageComponent
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
