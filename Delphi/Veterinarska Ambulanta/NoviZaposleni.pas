unit NoviZaposleni;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs, FMX.StdCtrls,
  FMX.Controls.Presentation, FMX.Edit, FMX.ListBox, FireDAC.Stan.Intf,
  FireDAC.Stan.Option, FireDAC.Stan.Param, FireDAC.Stan.Error, FireDAC.DatS,
  FireDAC.Phys.Intf, FireDAC.DApt.Intf, FireDAC.Stan.Async, FireDAC.DApt,
  Data.DB, FireDAC.Comp.DataSet, FireDAC.Comp.Client, FMX.Objects;

type
  TfrmNoviZaposleni = class(TForm)
    Ime: TEdit;
    Prezime: TEdit;
    User: TEdit;
    Email: TEdit;
    Lozinka: TEdit;
    Ime_Label: TLabel;
    Prezime_Label: TLabel;
    Telefon: TEdit;
    User_Label: TLabel;
    Email_Label: TLabel;
    Lozinka_Label: TLabel;
    Telefon_Label: TLabel;
    Potvrdi: TButton;
    Nazad: TButton;
    Tip_Label: TLabel;
    ComboBox1: TComboBox;
    Navbar: TImage;
    Forma: TPanel;
    StyleBook: TStyleBook;
    procedure NazadClick(Sender: TObject);
    procedure PotvrdiClick(Sender: TObject);
    procedure FormCreate(Sender: TObject);
    function GetTipIDZaposlenog(const imeTipa: string): Integer;
  private
    { Private declarations }
  public
    { Public declarations }
    ZaposleniTip: string;
    ZaposleniIme: string;
    ZaposleniPrezime: string;
    ZaposleniUser: string;
    ZaposleniEmail: string;
    ZaposleniLozinka: string;
    ZaposleniTelefon: string;
    ZaposleniTipID: Integer;
  end;

var
  frmNoviZaposleni: TfrmNoviZaposleni;

implementation

{$R *.fmx}
uses SpisakZaposlenih, Main, AdresaZaposlenih;

procedure TfrmNoviZaposleni.FormCreate(Sender: TObject);
var
MyQuery: TFDQuery;
begin
  MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'SELECT * FROM Tip_Zaposlenog';
    MyQuery.Open;

     ComboBox1.Clear;
     while not MyQuery.Eof do
     begin
       ComboBox1.Items.Add(MyQuery.FieldByName('Naziv_Uloge').AsString);
       MyQuery.Next;
     end;
  finally
       MyQuery.Free;
  end;
end;

procedure TfrmNoviZaposleni.NazadClick(Sender: TObject);
begin
  frmZaposleni.Show;
  self.Hide;
end;

//procedure TfrmNoviZaposleni.ComboBox1Select(Sender: TObject);
 //var
// TipZaposlenog: string;
// MyQuery: TFDQuery;
//begin
  //   MyQuery := TFDQuery.Create(nil);
  //   try
   //    MyQuery.Connection := GlobalConnection;
  //     ComboBox1.Items.Clear;
   //    MyQuery.SQL.Text := 'SELECT * FROM Tip_Zaposlenog';
   //    MyQuery.ParamByName('Naziv_Uloge').AsString := TipZaposlenog;
   //    MyQuery.Open;

 //    finally
  //        MyQuery.Close;
  //   end;
//end;

procedure TfrmNoviZaposleni.PotvrdiClick(Sender: TObject);
begin
  ZaposleniTipID := GetTipIDZaposlenog(ComboBox1.Selected.Text);
  ZaposleniTip := ComboBox1.Selected.Text;
  ZaposleniIme := Ime.Text;
  ZaposleniPrezime := Prezime.Text;
  ZaposleniUser := User.Text;
  ZaposleniEmail := Email.Text;
  ZaposleniLozinka := Lozinka.Text;
  ZaposleniTelefon := Telefon.Text;

  frmAdresaZaposlenih.Show;
  self.Hide;
end;

function TfrmNoviZaposleni.GetTipIDZaposlenog(const imeTipa: string): Integer;
var
MyQuery: TFDQuery;
begin
Result := -1;
MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'SELECT IDUloge FROM Tip_Zaposlenog WHERE Naziv_Uloge = :ime';
    MyQuery.ParamByName('ime').AsString := imeTipa;
    MyQuery.Open;

    if not MyQuery.IsEmpty then
      Result := MyQuery.FieldByName('IDUloge').AsInteger;
  finally
    MyQuery.Free;
  end;
end;

end.
