import { Directive, ElementRef, OnInit, Renderer2, inject, input } from '@angular/core';
import { LangChangeEvent, TranslateService } from '@ngx-translate/core';

@Directive({
  standalone: true,
  selector: '[sysActiveMenu]',
})
export default class ActiveMenuDirective implements OnInit {
  sysActiveMenu = input();

  private readonly el = inject(ElementRef);
  private readonly renderer = inject(Renderer2);
  private readonly translateService = inject(TranslateService);

  ngOnInit(): void {
    this.translateService.onLangChange.subscribe((event: LangChangeEvent) => {
      this.updateActiveFlag(event.lang);
    });

    this.updateActiveFlag(this.translateService.currentLang);
  }

  updateActiveFlag(selectedLanguage: string): void {
    if (this.sysActiveMenu() === selectedLanguage) {
      this.renderer.addClass(this.el.nativeElement, 'active');
    } else {
      this.renderer.removeClass(this.el.nativeElement, 'active');
    }
  }
}
