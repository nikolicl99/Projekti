unit Finansije_GlavnaForma;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs,
  FMX.Controls.Presentation, FMX.StdCtrls, FMX.Objects;

type
  Tfrmfinansijemeni = class(TForm)
    Zaposleni: TButton;
    Transakcije: TButton;
    Odjava: TButton;
    Forma: TPanel;
    StyleBook: TStyleBook;
    Navbar: TImage;
    Obavestenja: TButton;
    OdobreneTransakcije: TButton;
    Preuzete: TButton;
    procedure ZaposleniClick(Sender: TObject);
    procedure OdjavaClick(Sender: TObject);
    procedure TransakcijeClick(Sender: TObject);
    procedure ObavestenjaClick(Sender: TObject);
    procedure OdobreneTransakcijeClick(Sender: TObject);
    procedure PreuzeteClick(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  frmfinansijemeni: Tfrmfinansijemeni;

implementation

{$R *.fmx}
uses Transakcije, Finansije_Plate, Finansije_Transakcije, SpisakZaposlenih_Finansije, LoginZaposleni, Finansije_Obavestenja;

procedure Tfrmfinansijemeni.ObavestenjaClick(Sender: TObject);
begin
frmFinansijeObavestenja.show;
self.hide;
end;

procedure Tfrmfinansijemeni.OdjavaClick(Sender: TObject);
begin
frmloginzaposleni.show;
self.hide;
end;

procedure Tfrmfinansijemeni.OdobreneTransakcijeClick(Sender: TObject);
begin
frmfinansijetransakcije.show;
self.hide;
end;

procedure Tfrmfinansijemeni.PreuzeteClick(Sender: TObject);
begin
frmfinansijeplate.show;
self.hide;
end;

procedure Tfrmfinansijemeni.TransakcijeClick(Sender: TObject);
begin
frmtransakcije.show;
self.hide;
end;

procedure Tfrmfinansijemeni.ZaposleniClick(Sender: TObject);
begin
frmZaposlenifinansije.show;
self.hide;
end;

end.
