unit CallCentarOdgovorVeterinara;

interface

uses
  Winapi.Windows, Winapi.Messages, System.SysUtils, System.Variants, System.Classes, Vcl.Graphics,
  Vcl.Controls, Vcl.Forms, Vcl.Dialogs, Vcl.StdCtrls, FireDAC.Stan.Intf,
  FireDAC.Stan.Option, FireDAC.Stan.Error, FireDAC.UI.Intf, FireDAC.Phys.Intf,
  FireDAC.Stan.Def, FireDAC.Stan.Pool, FireDAC.Stan.Async, FireDAC.Phys,
  FireDAC.Phys.SQLite, FireDAC.Phys.SQLiteDef, FireDAC.Stan.ExprFuncs,
  FireDAC.Phys.SQLiteWrapper.Stat, FireDAC.VCLUI.Wait, FireDAC.Stan.Param,
  FireDAC.DatS, FireDAC.DApt.Intf, FireDAC.DApt, Data.DB, FireDAC.Comp.DataSet,
  FireDAC.Comp.Client, Vcl.ExtCtrls, Vcl.ComCtrls, Vcl.Imaging.jpeg,
  Vcl.Imaging.pngimage;

type
  TCCForm3 = class(TForm)
    Label1: TLabel;
    Label2: TLabel;
    Button1: TButton;
    Label3: TLabel;
    Edit1: TEdit;
    RichEdit1: TRichEdit;
    Button2: TButton;
    Image1: TImage;
    Label4: TLabel;
    Label5: TLabel;
    Label6: TLabel;
    Label7: TLabel;
    Image2: TImage;
    Button3: TButton;
    procedure Button1Click(Sender: TObject);
    procedure Button2Click(Sender: TObject);
    procedure Button3Click(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  CCForm3: TCCForm3;

implementation

{$R *.dfm}
uses LoginZaposleni, Main, CallCentarPregledPitanja;

procedure TCCForm3.Button1Click(Sender: TObject);
var
  IDPitanja: Integer;
  Odgovor: string;
  MyQuery: TFDQuery;
begin
  IDPitanja := StrToInt(Edit1.Text);
  Odgovor := RichEdit1.Text;
  MyQuery:= TFDQuery.Create(nil);
  MyQuery.Connection := GlobalConnection;

  // Proveravamo da li postoji red u tabeli Call_Centar sa datim IDPitanja
  MyQuery.SQL.Clear;
  MyQuery.SQL.Add('SELECT * FROM Call_Centar WHERE IDPitanja = :IDPitanja');
  MyQuery.ParamByName('IDPitanja').AsInteger := IDPitanja;
  MyQuery.Open;

  if MyQuery.IsEmpty then
  begin
    // Ako ne postoji red za datu IDPitanja, prikazujemo poruku
    ShowMessage('ID pitanja nije pronaðen u tabeli Call_Centar.');
  end
  else
  begin
    // Ako postoji red, ažuriramo polje Odgovor
    MyQuery.SQL.Clear;
    MyQuery.SQL.Text := 'UPDATE Call_Centar SET Datum_Odgovora = :Datum_Odgovora, Vreme_Odgovora = :Vreme_Odgovora, Odgovor = :Odgovor ' +
    'WHERE IDPitanja = :IDPitanja';
    MyQuery.ParamByName('Datum_Odgovora').AsString := DateToStr(Date);
    MyQuery.ParamByName('Vreme_Odgovora').AsString := TimeToStr(Time);
    MyQuery.ParamByName('Odgovor').AsString := Odgovor;
    MyQuery.ParamByName('IDPitanja').AsInteger := IDPitanja;

    MyQuery.ExecSql;

    ShowMessage('Odgovor je uspešno ažuriran.');
  end;
end;

procedure TCCForm3.Button2Click(Sender: TObject);
begin
frmloginzaposleni.show;
self.hide;
end;

procedure TCCForm3.Button3Click(Sender: TObject);
begin
CCForm4.show;
self.hide;
end;

end.
