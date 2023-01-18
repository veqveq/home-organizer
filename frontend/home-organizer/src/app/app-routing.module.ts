import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {DictionaryRegisterComponent} from "./dictionary/pages/dictionary-register/dictionary-register.component";
import {DictionaryPageComponent} from "./dictionary/pages/dictionary-page/dictionary-page.component";
import {CookbookPageComponent} from "./cookbook/pages/cookbook-page/cookbook-page.component";

const routes: Routes = [
  {path: 'dictionaries', component: DictionaryRegisterComponent},
  {path: 'notes', component: DictionaryRegisterComponent},
  {path: 'tests', component: DictionaryRegisterComponent},
  {path: 'dictionaries/:id', component: DictionaryPageComponent},
  {path: 'cookbook', component: CookbookPageComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
