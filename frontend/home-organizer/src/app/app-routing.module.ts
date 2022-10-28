import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {DictionaryRegisterComponent} from "./pages/dictionary-register/dictionary-register.component";
import {DictionaryPageComponent} from "./pages/dictionary-page/dictionary-page.component";

const routes: Routes = [
  {path: 'dictionaries', component: DictionaryRegisterComponent},
  {path: 'notes', component: DictionaryRegisterComponent},
  {path: 'tests', component: DictionaryRegisterComponent},
  {path: 'dictionaries/:id', component: DictionaryPageComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
