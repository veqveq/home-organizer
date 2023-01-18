import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpParams} from "@angular/common/http";
import {ErrorService} from "../../core/services/error.service";
import {catchError, map, Observable, retry, throwError} from "rxjs";
import {Page} from "../../core/models/page";
import {RecipeFilter} from "../models/recipe-filter";
import {Recipe} from "../models/recipe";

@Injectable({
  providedIn: 'root'
})
export class RecipeService {
  ROOT_API: string = "http://localhost:8082/api/v1/recipes"
  LIMIT_API: string = "http://localhost:8082/api/v1/limits"

  constructor(
    private http: HttpClient,
    private errorService: ErrorService
  ) {
  }

  filter(filter: RecipeFilter, sort: string, page: number): Observable<Page<Recipe>> {
    return this.http.post<Page<Recipe>>(this.ROOT_API + '/filter', filter, {
      params: new HttpParams({
        fromObject: {
          sort: sort,
          page: page ? page : '',
        },
      })
    }).pipe(
      retry(2),
      catchError(this.handleError.bind(this))
  )
  }

  getLimit(endpoint: string): Observable<number> {
    return this.http.get<number>(this.LIMIT_API + '/' + endpoint)
  }

  private handleError(error: HttpErrorResponse) {
    this.errorService.handle(error.error.message)
    return throwError(() => error.message)
  }
}
