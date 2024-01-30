unit StariPregled;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs, FMX.Objects,
  FMX.StdCtrls, FMX.Edit, FMX.Controls.Presentation, FireDAC.Stan.Intf,
  FireDAC.Stan.Option, FireDAC.Stan.Param, FireDAC.Stan.Error, FireDAC.DatS,
  FireDAC.Phys.Intf, FireDAC.DApt.Intf, FireDAC.Stan.Async, FireDAC.DApt,
  Data.DB, FireDAC.Comp.DataSet, FireDAC.Comp.Client;

type
  TfrmStariPregled = class(TForm)
    Forma: TPanel;
    Nazad: TButton;
    ImeLjubimca: TEdit;
    VrstaLjubimca: TEdit;
    ImeVlasnika: TEdit;
    Datum: TEdit;
    Vreme: TEdit;
    Simptomi: TEdit;
    Terapija: TEdit;
    PrezimeVlasnika: TEdit;
    ImeLjubimca_Label: TLabel;
    VrstaLjubimca_Label: TLabel;
    ImeVlasnika_Label: TLabel;
    PrezimeVlasnika_Label: TLabel;
    Datum_Label: TLabel;
    Vreme_Label: TLabel;
    Simptomi_Label: TLabel;
    Terapija_Label: TLabel;
    Lekovi: TButton;
    Navbar: TImage;
    StyleBook: TStyleBook;
    Rasa: TEdit;
    Rasa_Label: TLabel;
    procedure FormCreate(Sender: TObject);
    procedure NazadClick(Sender: TObject);
    procedure LekoviClick(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  FTerminID: Integer;
  FImeLjubimca: string;
  FVrstaLjubimca: string;
  FRasaLjubimca: string;
  FImeVlasnika: string;
  FPrezimeVlasnika: string;
  FDatum: TDate;
  FVreme: TTime;
  FVakcinacija: boolean;
  PregledID: integer;
  end;

var
  frmStariPregled: TfrmStariPregled;

implementation

{$R *.fmx}
uses Termini_Veterinar, Main, StariLekovi, StareVakcine;

procedure TfrmStariPregled.FormCreate(Sender: TObject);
var
MyQuery: TFDQuery;
begin
  ImeLjubimca.Text := FImeLjubimca;
  VrstaLjubimca.Text := FVrstaLjubimca;
  Rasa.Text := FRasaLjubimca;
  ImeVlasnika.Text := FImeVlasnika;
  PrezimeVlasnika.Text := FPrezimeVlasnika;
  Datum.Text := DateToStr(FDatum);
  Vreme.Text := TimeToStr(FVreme);
  MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'SELECT * FROM Pregled WHERE Termin = :TerminID';
    MyQuery.ParamByName('TerminID').AsInteger := FTerminID;
    MyQuery.Open;

    if not MyQuery.IsEmpty then
    begin
      Simptomi.Text := MyQuery.FieldByName('Simptomi').AsString;
      Terapija.Text := MyQuery.FieldByName('Terapija').AsString;
      PregledID := MyQuery.FieldByName('IDPregleda').AsInteger;
    end;
  finally
    MyQuery.Free;
  end;
end;

procedure TfrmStariPregled.LekoviClick(Sender: TObject);
begin
if FVakcinacija then
    begin
      frmstarevakcine.show;
      self.hide;
    end
    else
    begin
     frmstarilekovi.show;
     self.hide;
    end;
end;

procedure TfrmStariPregled.NazadClick(Sender: TObject);
begin
frmTerminiVeterinar.show;
self.hide;
end;

end.
